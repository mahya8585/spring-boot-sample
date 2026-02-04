import axios from 'axios';

// Create axios instance with base URL
const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Books API
export const booksApi = {
  // Get all books with pagination and search
  getBooks: (params = {}) => {
    const { page = 0, size = 10, sortBy = 'id', sortDir = 'asc', keyword = '' } = params;
    return api.get('/books', {
      params: { page, size, sortBy, sortDir, keyword }
    });
  },

  // Get book by ID
  getBook: (id) => {
    return api.get(`/books/${id}`);
  },

  // Get book by ISBN
  getBookByIsbn: (isbn13) => {
    return api.get(`/books/isbn/${isbn13}`);
  },

  // Create new book
  createBook: (bookData) => {
    return api.post('/books', bookData);
  },

  // Update book
  updateBook: (id, bookData) => {
    return api.put(`/books/${id}`, bookData);
  },

  // Delete book
  deleteBook: (id) => {
    return api.delete(`/books/${id}`);
  },
};

// Inventory API
export const inventoryApi = {
  // Get all inventory
  getInventory: () => {
    return api.get('/inventory');
  },

  // Get inventory by book ID
  getInventoryByBook: (bookId) => {
    return api.get(`/inventory/${bookId}`);
  },

  // Get inventory alerts
  getInventoryAlerts: () => {
    return api.get('/inventory/alerts');
  },

  // Get out of stock items
  getOutOfStockItems: () => {
    return api.get('/inventory/out-of-stock');
  },

  // Receive stock
  receiveStock: (data) => {
    return api.post('/inventory/receive', data);
  },

  // Sell stock
  sellStock: (data) => {
    return api.post('/inventory/sell', data);
  },

  // Adjust stock
  adjustStock: (data) => {
    return api.put('/inventory/adjust', data);
  },

  // Advanced inventory management APIs - Phase 1
  
  // Get transaction history for inventory item
  getTransactionHistory: (inventoryId) => {
    return api.get(`/inventory/${inventoryId}/transactions`);
  },

  // Get active reservations for inventory item
  getActiveReservations: (inventoryId) => {
    return api.get(`/inventory/${inventoryId}/reservations`);
  },

  // Transfer stock between store and warehouse
  transferStock: (transferData) => {
    return api.post('/inventory/transfers', transferData);
  },

  // Create stock reservation
  createReservation: (reservationData) => {
    return api.post('/inventory/reservations', reservationData);
  },

  // Release stock reservation
  releaseReservation: (reservationId) => {
    return api.delete(`/inventory/reservations/${reservationId}`);
  },

  // Process barcode scan
  processBarcodeScanned: (barcodeData) => {
    return api.post('/inventory/barcode-scan', barcodeData);
  },
};

// Orders API
export const ordersApi = {
  // Get all orders with pagination and filters
  getOrders: (params = {}) => {
    const { 
      page = 0, 
      size = 10, 
      sortBy = 'orderDate', 
      sortDir = 'desc', 
      status, 
      type, 
      customerId, 
      startDate, 
      endDate, 
      keyword 
    } = params;
    
    const queryParams = { page, size, sortBy, sortDir };
    if (status) queryParams.status = status;
    if (type) queryParams.type = type;
    if (customerId) queryParams.customerId = customerId;
    if (startDate) queryParams.startDate = startDate;
    if (endDate) queryParams.endDate = endDate;
    if (keyword) queryParams.keyword = keyword;
    
    return api.get('/orders', { params: queryParams });
  },

  // Get order by ID
  getOrder: (id) => {
    return api.get(`/orders/${id}`);
  },

  // Get order by order number
  getOrderByNumber: (orderNumber) => {
    return api.get(`/orders/number/${orderNumber}`);
  },

  // Get orders by customer ID
  getOrdersByCustomer: (customerId) => {
    return api.get(`/orders/customer/${customerId}`);
  },

  // Create new order
  createOrder: (orderData) => {
    return api.post('/orders', orderData);
  },

  // Confirm order
  confirmOrder: (id) => {
    return api.post(`/orders/${id}/confirm`);
  },

  // Mark order as picking
  pickOrder: (id) => {
    return api.post(`/orders/${id}/pick`);
  },

  // Mark order as shipped
  shipOrder: (id) => {
    return api.post(`/orders/${id}/ship`);
  },

  // Mark order as delivered
  deliverOrder: (id) => {
    return api.post(`/orders/${id}/deliver`);
  },

  // Get order status counts
  getStatusCounts: () => {
    return api.get('/orders/stats/status-count');
  },
};

// Customers API
export const customersApi = {
  // Get all customers with pagination and filters
  getCustomers: (params = {}) => {
    const { 
      page = 0, 
      size = 20, 
      sort = 'name,asc', 
      keyword, 
      customerType, 
      status, 
      startDate, 
      endDate 
    } = params;
    
    const queryParams = { page, size, sort };
    if (keyword) queryParams.keyword = keyword;
    if (customerType) queryParams.customerType = customerType;
    if (status) queryParams.status = status;
    if (startDate) queryParams.startDate = startDate;
    if (endDate) queryParams.endDate = endDate;
    
    return api.get('/customers', { params: queryParams });
  },

  // Get customer by ID
  getCustomer: (id) => {
    return api.get(`/customers/${id}`);
  },

  // Search customers
  searchCustomers: (keyword, params = {}) => {
    const { page = 0, size = 20 } = params;
    return api.get('/customers/search', {
      params: { keyword, page, size }
    });
  },

  // Get customer orders
  getCustomerOrders: (id) => {
    return api.get(`/customers/${id}/orders`);
  },

  // Get customer statistics
  getCustomerStats: () => {
    return api.get('/customers/stats');
  },

  // Create new customer
  createCustomer: (customerData) => {
    return api.post('/customers', customerData);
  },

  // Update customer
  updateCustomer: (id, customerData) => {
    return api.put(`/customers/${id}`, customerData);
  },

  // Delete customer (logical delete)
  deleteCustomer: (id) => {
    return api.delete(`/customers/${id}`);
  },
};

// Request interceptor
api.interceptors.request.use(
  (config) => {
    // Add auth token if available
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Handle common errors
    if (error.response?.status === 401) {
      // Unauthorized - redirect to login
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;