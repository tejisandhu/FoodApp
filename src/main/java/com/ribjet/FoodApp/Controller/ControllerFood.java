package com.ribjet.FoodApp.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ribjet.FoodApp.Entity.User;
import com.ribjet.FoodApp.Entity.UserType;
import com.ribjet.FoodApp.Repository.UserTypeRepository;
import com.ribjet.FoodApp.Service.CustomerService;
import com.ribjet.FoodApp.Service.EmailService;
import com.ribjet.FoodApp.Service.MenuService;
import com.ribjet.FoodApp.Service.OrderService;
import com.ribjet.FoodApp.Service.RestaurantService;
import com.ribjet.FoodApp.Service.UserService;
import com.ribjet.FoodApp.util.FileUploadUtil;
import com.ribjet.FoodApp.util.PendingUser;
import com.ribjet.FoodApp.Entity.Order;
import com.ribjet.FoodApp.Entity.OrderItem;
import com.ribjet.FoodApp.Repository.MenuRepository;
import com.ribjet.FoodApp.Repository.OrderItemRepository;
import com.ribjet.FoodApp.Repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.ribjet.FoodApp.Entity.Customer;
import com.ribjet.FoodApp.Entity.Menu;
import com.ribjet.FoodApp.Entity.Restaurant;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
public class ControllerFood {

	@Autowired
	private UserTypeRepository userTypeRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private RestaurantService restaurantService;
	@Autowired
	private MenuService menuService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private MenuRepository menuRepository;

	@Autowired
	private OrderService orderService;
	@Autowired
	private EmailService emailService;

	@GetMapping("/")
	public String Home() {
		return "index";
	}

	@GetMapping("/register")
	public String registerPage(Model model) {

		List<UserType> userTypes = userTypeRepository.findAll();
		System.out.print(userTypes.get(0));
		model.addAttribute("getAllTypes", userTypes);
		model.addAttribute("user", new User());

		return "register";
	}

	// Process new user registration (before finalizing)
	@PostMapping("/register/new")
	public String newUser(@ModelAttribute("user") User user, Model model, @RequestParam("email") String email,
			@RequestParam("type") String userTypeIdStr, @RequestParam("cp") String cp,
			RedirectAttributes redirectAttributes, HttpSession session) {
		Integer userTypeId = Integer.valueOf(userTypeIdStr);

		// Check if user already exists
		Optional<User> existingUser = userService.findByEmail(user.getEmail());
		if (existingUser.isPresent()) {
			model.addAttribute("emailExist", 2);
			List<UserType> userTypes = userTypeRepository.findAll();
			model.addAttribute("getAllTypes", userTypes);
			model.addAttribute("user", new User());
			return "register";
		}

		// Validate password confirmation
		if (!cp.equals(user.getPassword())) {
			model.addAttribute("message", 1);
			List<UserType> userTypes = userTypeRepository.findAll();
			model.addAttribute("getAllTypes", userTypes);
			model.addAttribute("user", new User());
			return "register";
		}

		UserType userType = userTypeRepository.findById(userTypeId).orElse(null);
		user.setUserType(userType);

		String otp = generateOTP();

		String subject = "OTP Verification for Registration";
		String body = "<html>" + "<body style='font-family: Arial, sans-serif; padding: 20px;'>"
				+ "<div style='max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; background-color: #f9f9f9;'>"
				+ "<h2 style='color: #2d89ef; text-align: center;'>OTP Verification</h2>"
				+ "<p style='font-size: 16px; color: #333;'>Dear " + email + ",</p>"
				+ "<p style='font-size: 16px; color: #333;'>Your OTP for registration is: <strong>" + otp
				+ "</strong></p>"
				+ "<p style='font-size: 16px; color: #333;'>Please enter this OTP on the verification page to complete your registration.</p>"
				+ "<br>"
				+ "<p style='font-size: 14px; color: #777; text-align: center;'>If you did not request this, please ignore this email.</p>"
				+ "</div>" + "</body>" + "</html>";

		try {
			emailService.sendEmail(email, subject, body);
		} catch (MessagingException e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "Error sending OTP email. Please try again.");
			return "redirect:/register";
		}

		PendingUser pendingUser = new PendingUser(user, otp);
		session.setAttribute("pendingUser", pendingUser);
		return "redirect:/otp";
	}

	@GetMapping("/otp")
	public String otpVerificationPage() {
		return "otp";
	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") String enteredOtp,

			RedirectAttributes redirectAttributes, HttpSession session) {
		PendingUser pendingUser = (PendingUser) session.getAttribute("pendingUser");

		if (pendingUser.getOtp().equals(enteredOtp)) {
			userService.addNew(pendingUser.getUser());

			String subject = "Registered Successfully";
			String email = pendingUser.getUser().getEmail();
			String body = "<html>" + "<body style='font-family: Arial, sans-serif; padding: 20px;'>"
					+ "<div style='max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; background-color: #f9f9f9;'>"
					+ "<h2 style='color: #2d89ef; text-align: center;'>Congratulations!</h2>"
					+ "<p style='font-size: 16px; color: #333;'>Dear " + email + ",</p>"
					+ "<p style='font-size: 16px; color: #333;'>You have successfully registered.</p>"
					+ "<p style='font-size: 16px; color: #333;'>We are excited to have you with us.</p>" + "<br>"
					+ "<p style='text-align: center;'><a href='https://yourwebsite.com' style='background-color: #2d89ef; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Visit Our Website</a></p>"
					+ "<br>"
					+ "<p style='font-size: 14px; color: #777; text-align: center;'>If you did not register, please ignore this email.</p>"
					+ "</div>" + "</body>" + "</html>";
			try {
				emailService.sendEmail(email, subject, body);
			} catch (MessagingException e) {
				e.printStackTrace();
			}

			redirectAttributes.addFlashAttribute("message", "Registration completed successfully.");
			return "redirect:/auth/login";
		} else {
			redirectAttributes.addFlashAttribute("error", "Invalid OTP. Please try again.");
			return "redirect:/otp";
		}
	}

	// Simple OTP generator (6-digit numeric)
	private String generateOTP() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}

	// Login Page
	@GetMapping("/auth/login")
	public String showLoginPage() {
		return "login";
	}

	// Process Login
	@PostMapping("/auth/login")
	public String login(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {

		Optional<User> user = userService.findByEmail(email);

		if (user.isPresent()) {
			User foundUser = user.get();
			if (foundUser.getPassword().equals(password)) {
				session.setAttribute("user", foundUser);

				// Retrieve the original URL if available
				String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
				System.out.println("Redirecting to: " + redirectUrl); // Debugging

				session.removeAttribute("redirectAfterLogin"); // Clear it after use

				if (redirectUrl != null) {
					return "redirect:" + redirectUrl; // Redirect to original page
				}

				if ("restaurant".equalsIgnoreCase(foundUser.getUserType().getUserTypeName())) {
					return "redirect:/restaurant/dashboard";
				} else if ("CUSTOMER".equalsIgnoreCase(foundUser.getUserType().getUserTypeName())) {
					return "redirect:/customer/dashboard";
				}
			}
		}

		// If login fails, return to login page with an error message
		model.addAttribute("error", true);
		return "login";
	}

	// Logout
	@GetMapping("/auth/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/auth/login";
	}

	// Restaurant Dashboard
	@GetMapping("/restaurant/dashboard")
	public String adminDashboard(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user == null || !"restaurant".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
			return "redirect:/auth/login";
		}
		model.addAttribute("user", user);
		List<Restaurant> restaurant = restaurantService.getRestaurantsByOwnerId(user.getId());

		if (restaurant.isEmpty()) {
			model.addAttribute("restaurant", null); // Set restaurant as null if none exist
			System.out.println("No restaurant found for user: " + user.getId());
		} else {
			model.addAttribute("restaurant", restaurant.get(0)); // Set the first restaurant
			System.out.println("Restaurant found: " + restaurant.get(0));
		}

		return "restaurant-dashboard";
	}

	@GetMapping("/restaurant/dashboard/viewMenu")
	public String restaurantMenuView(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user == null || !"restaurant".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
			return "redirect:/auth/login";
		}
		model.addAttribute("user", user);
		List<Restaurant> restaurant = restaurantService.getRestaurantsByOwnerId(user.getId());

		if (restaurant.isEmpty()) {
			model.addAttribute("restaurant", null); // Set restaurant as null if none exist
			System.out.println("No restaurant found for user: " + user.getId());
		} else {
			model.addAttribute("restaurant", restaurant.get(0)); // Set the first restaurant
			System.out.println("Restaurant found: " + restaurant.get(0));
		}

		return "view-restaurant-menu";
	}

	// Edit Restaurant Data
	@GetMapping("/restaurant/dashboard/edit")
	public String editRestaurantData(Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null || !"restaurant".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
			return "redirect:/auth/login";
		}
		model.addAttribute("user", user);
		List<Restaurant> restaurant = restaurantService.getRestaurantsByOwnerId(user.getId());

		if (restaurant.isEmpty()) {
			model.addAttribute("restaurant", null); // Set restaurant as null if none exist
			System.out.println("No restaurant found for user: " + user.getId());
		} else {
			model.addAttribute("restaurant", restaurant.get(0)); // Set the first restaurant
			System.out.println("Restaurant found: " + restaurant.get(0));
		}

		return "edit-restaurant";
	}

	@PostMapping("/dashboard/restaurant/edit")
	public String editRestaurantDataSave(@ModelAttribute Restaurant restaurant,
			@RequestParam("imageFile") MultipartFile image, Model model, HttpSession session) {
		System.out.println(" Method editRestaurantDataSave is called!");

		System.out.println("File received: " + !image.isEmpty());
		User user = (User) session.getAttribute("user");
		if (user == null || !"restaurant".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
			return "redirect:/auth/login";
		}
		restaurant.setOwner(user);
		System.out.println(restaurant.getOwner().getId());
		// Debugging: Print if file is received
		System.out.println("File received: " + !image.isEmpty());
		System.out.println("Original file name: " + image.getOriginalFilename());

		// Handle Image Upload
		if (!image.isEmpty()) {
			String fileName = "Owner_" + user.getId() + "__" + System.currentTimeMillis() + "_"
					+ StringUtils.cleanPath(image.getOriginalFilename());

			String uploadDir = "src/main/resources/static/uploads/restaurants/";

			try {
				FileUploadUtil.saveFile(uploadDir, fileName, image);
				restaurant.setImage(fileName); // Store file name in DB
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// Keep old image if no new one is uploaded
			Restaurant existingRestaurant = restaurantService.getRestaurantById(restaurant.getRestaurantId());
			restaurant.setImage(existingRestaurant.getImage());
		}

		restaurantService.editRestaurant(restaurant);
		return "redirect:/restaurant/dashboard";
	}

	// MENU
	// Add a new menu item
	@GetMapping("/restaurant/dashboard/menu/add")
	public String addMenuItem(Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null || !"restaurant".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
			return "redirect:/auth/login";
		}
		List<Restaurant> restaurantList = restaurantService.getRestaurantsByOwnerId(user.getId());

		Restaurant restaurant = restaurantList.get(0);
		Menu menu = new Menu();
		menu.setRestaurant(restaurant);
		model.addAttribute("menu", menu);
		return "addMenuItem";
	}

	@PostMapping("/restaurant/dashboard/menu/save")
	public String saveMenuItem(@ModelAttribute("menu") Menu menu, Model model, HttpSession session,
			@RequestParam("imageFile") MultipartFile image) {
		User user = (User) session.getAttribute("user");
		if (user == null || !"restaurant".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
			return "redirect:/auth/login";
		}
		List<Restaurant> restaurantList = restaurantService.getRestaurantsByOwnerId(user.getId());

		Restaurant restaurant = restaurantList.get(0);
		menu.setRestaurant(restaurant);

		// Handle Image Upload
		if (!image.isEmpty()) {
			String fileName = "Restaurant_" + restaurant.getRestaurantId() + "__" + System.currentTimeMillis() + "_"
					+ StringUtils.cleanPath(image.getOriginalFilename());

			String uploadDir = "src/main/resources/static/uploads/restaurants/menu";

			try {
				FileUploadUtil.saveFile(uploadDir, fileName, image);
				menu.setImage(fileName); // Store file name in DB
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		menuService.save(menu);

		return "redirect:/restaurant/dashboard";
	}

	@GetMapping("/restaurant/dashboard/menu/edit")
	public String editMenuItem(Model model, HttpSession session, @RequestParam(required = false) Integer itemId) {
		User user = (User) session.getAttribute("user");
		if (user == null || !"restaurant".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
			return "redirect:/auth/login";
		}
		List<Restaurant> restaurantList = restaurantService.getRestaurantsByOwnerId(user.getId());

		Restaurant restaurant = restaurantList.get(0);
		Menu menu = menuService.getMenuItemById(itemId);
		menu.setRestaurant(restaurant);
		model.addAttribute("menu", menu);
		return "editMenuItem";

	}

	@PostMapping("/restaurant/dashboard/menu/edit/save")
	public String editMenuItemSave(@ModelAttribute("menu") Menu menu, Model model, HttpSession session,
			@RequestParam("imageFile") MultipartFile image) {

		User user = (User) session.getAttribute("user");
		if (user == null || !"restaurant".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
			return "redirect:/auth/login";
		}

		List<Restaurant> restaurantList = restaurantService.getRestaurantsByOwnerId(user.getId());
		if (restaurantList.isEmpty()) {
			return "redirect:/restaurant/dashboard"; // Redirect if restaurant is not found
		}

		Restaurant restaurant = restaurantList.get(0);
		// Fetch existing menu item
		Menu existingMenu = menuService.getMenuItemById(menu.getItemId());

		if (existingMenu != null) {
			// Update existing menu item details
			existingMenu.setItemId(menu.getItemId());
			existingMenu.setName(menu.getName());
			existingMenu.setPrice(menu.getPrice());
			existingMenu.setDescription(menu.getDescription());
			existingMenu.setRestaurant(restaurant); // Ensure restaurant remains the same
			existingMenu.setAvailability(menu.getAvailability());
			// Handle Image Upload
			if (!image.isEmpty()) {
				String fileName = "Restaurant_" + restaurant.getRestaurantId() + "__" + System.currentTimeMillis()
						+ "_"
						+ StringUtils.cleanPath(image.getOriginalFilename());

				String uploadDir = "src/main/resources/static/uploads/restaurants/menu";
				try {
					FileUploadUtil.saveFile(uploadDir, fileName, image);
					existingMenu.setImage(fileName); // Update image only if a new one is uploaded
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			menuService.save(existingMenu); // Save the updated menu item
		}

		return "redirect:/restaurant/dashboard";
	}

	// SHOW ORDERS TO THE RESTAURANT
	@GetMapping("/restaurant/dashboard/orders")
	public String viewRestaurantOrders(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/auth/login";
		}

		Restaurant restaurant = restaurantService.getRestaurantByOwner(user);
		if (restaurant == null) {
			return "redirect:/restaurant/dashboard";
		}

		List<Order> restaurantOrders = orderService.getOrdersForRestaurant(restaurant);
		model.addAttribute("restaurantOrders", restaurantOrders);
		// System.out.println(restaurantOrders.get(0).getPaymentStatus());

		return "restaurant-orders"; // Thymeleaf template to display orders
	}

	@PostMapping("/restaurant/dashboard/orders/update")
	public String updateOrderStatus(@RequestParam("orderId") int orderId, @RequestParam("status") String newStatus,
			RedirectAttributes redirectAttributes) {
		Order order = orderService.getOrderById(orderId);
		String currentStatus = order.getStatus();

		// Valid status transitions
		if ((currentStatus.equals("PENDING") && newStatus.equals("CONFIRMED"))
				|| (currentStatus.equals("CONFIRMED") && newStatus.equals("DELIVERED"))) {

			orderService.updateOrderStatus(orderId, newStatus);

		} else {
			String errorMessage = "Invalid status transition: Order #" + orderId + " cannot change from "
					+ currentStatus + " to " + newStatus;
			redirectAttributes.addFlashAttribute("error", errorMessage);
		}

		return "redirect:/restaurant/dashboard/orders";
	}

	// Customer Dashboard
	@GetMapping("/customer/dashboard")
	public String customerDashboard(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
			return "redirect:/auth/login";
		}
		model.addAttribute("user", user);

		List<Restaurant> restaurant = restaurantService.getAll();
		model.addAttribute("restaurant", restaurant);
		return "customer-dashboard";
	}

	@GetMapping("/customer/dashboard/viewRestaurant")
	public String customerViewRestaurant(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
			return "redirect:/auth/login";
		}
		model.addAttribute("user", user);

		List<Restaurant> restaurant = restaurantService.getAll();
		model.addAttribute("restaurant", restaurant);
		return "customer-view-restaurant";
	}

	// Edit Customer Data
	@GetMapping("/customer/dashboard/edit")
	public String editCustomerData(Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null || !"customer".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
			return "redirect:/auth/login";
		}
		model.addAttribute("user", user);
		return "edit-customer";
	}

	@PostMapping("/customer/dashboard/edit")
	public String editCustomerDataSave(Model model, HttpSession session, @ModelAttribute("user") User userData,
			@RequestParam("imageFile") MultipartFile image) {
		User user = (User) session.getAttribute("user");
		if (user == null || !"customer".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
			return "redirect:/auth/login";
		}
		userData.setId(user.getId());
		userData.setEmail(user.getEmail());
		userData.setPassword(user.getPassword());
		userData.setRegistrationDate(user.getRegistrationDate());
		userData.setUserType(user.getUserType());
		// Handle Image Upload
		if (!image.isEmpty()) {
			String fileName = "CUSTOMER_" + user.getId() + "__" + System.currentTimeMillis() + "_"
					+ StringUtils.cleanPath(image.getOriginalFilename());
			String uploadDir = "src/main/resources/static/uploads/customer";
			try {
				FileUploadUtil.saveFile(uploadDir, fileName, image);
				userData.setImage(fileName); // Store file name in DB
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		userService.save(userData);
		session.setAttribute("user", userData);

		return "redirect:/customer/dashboard";
	}

	@GetMapping("/customer/dashboard/restaurant/menu")
	public String showRestaurantMenu(Model model, HttpSession session,
			@RequestParam(required = false) Integer restaurantId) {
		User user = (User) session.getAttribute("user");
		if (user == null || !"customer".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
			return "redirect:/auth/login";
		}

		System.out.println("REstaurnat id is : " + restaurantId);

		List<Menu> menu = menuService.getAllItems(restaurantId);
		System.out.println("MENU:::" + menu);
		model.addAttribute("menu", menu);
		model.addAttribute("restaurantId", restaurantId);
		return "showMenu";
	}

	@GetMapping("/customer/dashboard/restaurant/menu/cart")
	public String showCartPage(@RequestParam(value = "restaurantId", required = false) Integer restaurantId,
			HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user == null || !"customer".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
			return "redirect:/auth/login";
		}
		Customer customer = customerService.findByUserId(user.getId());
		// Pass customer and address to the model
		model.addAttribute("customer", customer);
		model.addAttribute("customerAddress", customer.getAddress()); // Pass address separately
		model.addAttribute("restaurantId", restaurantId);
		System.out.println("RESTAURANT VALUE SENT TO CART:" + restaurantId);
		return "cart";
	}

	@PostMapping("/customer/dashboard/restaurant/menu/cart/placeOrder")
	public String placeOrder(@RequestParam(value = "restaurantId", required = false) Integer restaurantId,
			@RequestParam("orderData") String orderData,
			@RequestParam(value = "address", required = false) String address, HttpSession session,
			RedirectAttributes redirectAttributes) {

		User user = (User) session.getAttribute("user");
		if (user == null || !"customer".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
			return "redirect:/auth/login";
		}

		Customer customer = customerService.findByOwner(user);
		if (customer == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "Customer not found.");
			return "redirect:/customer/dashboard/restaurant/menu/cart";
		}

		if ((customer.getAddress() == null || customer.getAddress().isEmpty())
				&& (address == null || address.isEmpty())) {
			redirectAttributes.addFlashAttribute("errorMessage", "Please provide a delivery address.");
			return "redirect:/customer/dashboard/restaurant/menu/cart";
		}

		if (address != null && !address.isEmpty()) {
			customer.setAddress(address);
			customerService.save(customer);
		}

		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> items;
		try {
			items = objectMapper.readValue(orderData, new TypeReference<List<Map<String, Object>>>() {
			});
		} catch (JsonProcessingException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Invalid order data.");
			return "redirect:/customer/dashboard/restaurant/menu/cart";
		}

		if (items.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Cart is empty!");
			return "redirect:/customer/dashboard/restaurant/menu/cart";
		}

		// Extract restaurantId from the first item – ensure your JSON contains
		// "restaurantId"
		// Integer restaurantId =
		// Integer.parseInt(items.get(0).get("itemId").toString());
		System.out.println("RESTAURANT ID IS THIS: " + restaurantId);
		Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
		if (restaurant == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "Invalid restaurant.");
			return "redirect:/customer/dashboard/restaurant/menu/cart";
		}

		double totalAmount = 0.0;
		Map<Integer, Integer> orderItems = new HashMap<>();
		for (Map<String, Object> item : items) {
			// Make sure all keys exist; if missing, handle appropriately.
			if (item.get("itemId") == null || item.get("quantity") == null || item.get("price") == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "Cart item data is missing.");
				return "redirect:/customer/dashboard/restaurant/menu/cart";
			}
			Integer menuId = Integer.parseInt(item.get("itemId").toString());
			Integer quantity = Integer.parseInt(item.get("quantity").toString());
			double price = Double.parseDouble(item.get("price").toString());
			orderItems.put(menuId, quantity);
			totalAmount += price * quantity;
		}

		try {
			// Create a Razorpay order
			RazorpayClient razorpay = new RazorpayClient("rzp_test_svz7EHcXDrsCHk", "MEBODuyLWJcNtCLQro4YIpZ7");
			JSONObject options = new JSONObject();
			options.put("amount", (int) (totalAmount * 100)); // amount in paise
			options.put("currency", "INR");
			options.put("receipt", "txn_" + System.currentTimeMillis());
			options.put("payment_capture", 1);

			com.razorpay.Order razorpayOrder = razorpay.orders.create(options);
			// Check if the order response has an "id"
			if (!razorpayOrder.has("id")) {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to generate Razorpay order.");
				return "redirect:/customer/dashboard/restaurant/menu/cart";
			}
			String razorpayOrderId = razorpayOrder.get("id").toString();

			// Create the order entity
			Order newOrder = orderService.placeOrder(customer, restaurant, orderItems, address);
			newOrder.setRazorpayOrderId(razorpayOrderId);
			newOrder.setPaymentStatus("COD");

			// Save the order
			newOrder = orderRepository.save(newOrder);

			// Save order items (associating them with the new order)
			for (Map.Entry<Integer, Integer> entry : orderItems.entrySet()) {
				Integer menuId = entry.getKey();
				Integer quantity = entry.getValue();
				Menu menuItem = menuRepository.findById(menuId).orElse(null);
				if (menuItem == null) {
					redirectAttributes.addFlashAttribute("errorMessage", "Invalid menu item.");
					return "redirect:/customer/dashboard/restaurant/menu/cart";
				}
				OrderItem orderItem = new OrderItem();
				orderItem.setOrder(newOrder);
				orderItem.setMenuItem(menuItem);
				orderItem.setQuantity(quantity);
				orderItem.setPrice(menuItem.getPrice() * quantity);
				orderItemRepository.save(orderItem);
			}

			// // Pass data to payment page via flash attributes
			// redirectAttributes.addFlashAttribute("orderId", newOrder.getOrderId());
			// redirectAttributes.addFlashAttribute("totalPrice", newOrder.getTotalPrice());
			// redirectAttributes.addFlashAttribute("razorpayOrderId", razorpayOrderId);
			session.setAttribute("orderId", newOrder.getOrderId());
			session.setAttribute("totalPrice", newOrder.getTotalPrice());
			session.setAttribute("razorpayOrderId", razorpayOrderId);
			return "redirect:/customer/dashboard/payment";

			// return "redirect:/customer/dashboard/payment?orderId="
			// + newOrder.getOrderId()
			// + "&totalPrice=" + newOrder.getTotalPrice()
			// + "&razorpayOrderId=" + razorpayOrderId;
		} catch (RazorpayException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Payment initialization failed: " + e.getMessage());
			return "redirect:/customer/dashboard/restaurant/menu/cart";
		}
	}

	@GetMapping("/customer/dashboard/payment")
	public String showPaymentPage(
			Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		Integer orderId = (Integer) session.getAttribute("orderId");
		Double totalPrice = (Double) session.getAttribute("totalPrice");
		String razorpayOrderId = (String) session.getAttribute("razorpayOrderId");

		if (orderId == null || totalPrice == null || razorpayOrderId == null) {
			return "redirect:/customer/dashboard/restaurant/menu/cart"; // Redirect if session data is missing
		}

		model.addAttribute("orderId", orderId);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("razorpayOrderId", razorpayOrderId);

		return "payment";
	}

	@GetMapping("/customer/dashboard/payment/success")
	public String paymentSuccess(@RequestParam("orderId") Integer orderId,
			@RequestParam("payment_id") String paymentId,
			HttpSession session, RedirectAttributes redirectAttributes) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/auth/login";
		}

		// Fetch customer using the repository
		Customer customer = customerService.findByOwner(user);
		if (customer == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "Customer not found.");
			return "redirect:/customer/dashboard/restaurant/menu/cart";
		}

		// Find the latest order for the customer (or use a more specific lookup if
		// needed)
		Order order = orderRepository.findTopByCustomerOrderByOrderTimeDesc(customer);
		System.out.println("ORDER AFTER PAYMENT IS " + order);
		if (order != null) {
			order.setPaymentStatus("Paid");
			order.setPaymentId(paymentId); // Optionally store Razorpay payment id
			order.setPaid(true); // If you add an isPaid field in your Order entity
			orderRepository.save(order);
		}

		redirectAttributes.addFlashAttribute("successMessage", "Payment successful! Your order is confirmed.");
		return "redirect:/customer/dashboard/pastorders";
	}

	@GetMapping("/customer/dashboard/pastorders")
	public String viewPastOrders(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/auth/login";
		}

		Customer customer = customerService.findByOwner(user);
		if (customer == null) {
			return "redirect:/customer/dashboard";
		}

		List<Order> pastOrders = orderService.getPastOrders(customer);
		model.addAttribute("pastOrders", pastOrders);

		return "past-orders"; // This is the Thymeleaf template for displaying past orders
	}

}
