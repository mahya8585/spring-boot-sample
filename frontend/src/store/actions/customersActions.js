import { customersApi } from '../../services/api';

// Action types
export const CUSTOMERS_LOADING = 'CUSTOMERS_LOADING';
export const CUSTOMERS_SUCCESS = 'CUSTOMERS_SUCCESS';
export const CUSTOMERS_ERROR = 'CUSTOMERS_ERROR';
export const CUSTOMER_DETAIL_LOADING = 'CUSTOMER_DETAIL_LOADING';
export const CUSTOMER_DETAIL_SUCCESS = 'CUSTOMER_DETAIL_SUCCESS';
export const CUSTOMER_DETAIL_ERROR = 'CUSTOMER_DETAIL_ERROR';
export const CUSTOMER_CREATE_LOADING = 'CUSTOMER_CREATE_LOADING';
export const CUSTOMER_CREATE_SUCCESS = 'CUSTOMER_CREATE_SUCCESS';
export const CUSTOMER_CREATE_ERROR = 'CUSTOMER_CREATE_ERROR';
export const CUSTOMER_UPDATE_LOADING = 'CUSTOMER_UPDATE_LOADING';
export const CUSTOMER_UPDATE_SUCCESS = 'CUSTOMER_UPDATE_SUCCESS';
export const CUSTOMER_UPDATE_ERROR = 'CUSTOMER_UPDATE_ERROR';
export const CUSTOMER_DELETE_LOADING = 'CUSTOMER_DELETE_LOADING';
export const CUSTOMER_DELETE_SUCCESS = 'CUSTOMER_DELETE_SUCCESS';
export const CUSTOMER_DELETE_ERROR = 'CUSTOMER_DELETE_ERROR';
export const CUSTOMER_ORDERS_LOADING = 'CUSTOMER_ORDERS_LOADING';
export const CUSTOMER_ORDERS_SUCCESS = 'CUSTOMER_ORDERS_SUCCESS';
export const CUSTOMER_ORDERS_ERROR = 'CUSTOMER_ORDERS_ERROR';
export const CUSTOMER_STATS_LOADING = 'CUSTOMER_STATS_LOADING';
export const CUSTOMER_STATS_SUCCESS = 'CUSTOMER_STATS_SUCCESS';
export const CUSTOMER_STATS_ERROR = 'CUSTOMER_STATS_ERROR';
export const CLEAR_CUSTOMER_ERROR = 'CLEAR_CUSTOMER_ERROR';

/**
 * Helper function to extract error message from API response
 */
const getErrorMessage = (error, defaultMessage) => {
  if (error.response?.data?.message) {
    return error.response.data.message;
  }
  if (error.response?.data?.fieldErrors) {
    const fieldErrors = error.response.data.fieldErrors;
    const messages = Object.values(fieldErrors);
    return messages.length > 0 ? messages.join(', ') : defaultMessage;
  }
  return error.message || defaultMessage;
};

/**
 * Fetch customers with pagination and filters
 */
export const fetchCustomers = (params = {}) => async (dispatch) => {
  dispatch({ type: CUSTOMERS_LOADING });
  try {
    const response = await customersApi.getCustomers(params);
    dispatch({
      type: CUSTOMERS_SUCCESS,
      payload: response.data
    });
  } catch (error) {
    dispatch({
      type: CUSTOMERS_ERROR,
      payload: getErrorMessage(error, 'Failed to fetch customers')
    });
  }
};

/**
 * Search customers by keyword
 */
export const searchCustomers = (keyword, params = {}) => async (dispatch) => {
  dispatch({ type: CUSTOMERS_LOADING });
  try {
    const response = await customersApi.searchCustomers(keyword, params);
    dispatch({
      type: CUSTOMERS_SUCCESS,
      payload: response.data
    });
  } catch (error) {
    dispatch({
      type: CUSTOMERS_ERROR,
      payload: getErrorMessage(error, 'Failed to search customers')
    });
  }
};

/**
 * Fetch customer by ID
 */
export const fetchCustomerById = (id) => async (dispatch) => {
  dispatch({ type: CUSTOMER_DETAIL_LOADING });
  try {
    const response = await customersApi.getCustomer(id);
    dispatch({
      type: CUSTOMER_DETAIL_SUCCESS,
      payload: response.data
    });
  } catch (error) {
    dispatch({
      type: CUSTOMER_DETAIL_ERROR,
      payload: getErrorMessage(error, 'Failed to fetch customer details')
    });
  }
};

/**
 * Create new customer
 */
export const createCustomer = (customerData) => async (dispatch) => {
  dispatch({ type: CUSTOMER_CREATE_LOADING });
  try {
    const response = await customersApi.createCustomer(customerData);
    dispatch({
      type: CUSTOMER_CREATE_SUCCESS,
      payload: response.data
    });
    return response.data;
  } catch (error) {
    const errorMessage = getErrorMessage(error, 'Failed to create customer');
    dispatch({
      type: CUSTOMER_CREATE_ERROR,
      payload: errorMessage
    });
    throw new Error(errorMessage);
  }
};

/**
 * Update customer
 */
export const updateCustomer = (id, customerData) => async (dispatch) => {
  dispatch({ type: CUSTOMER_UPDATE_LOADING });
  try {
    const response = await customersApi.updateCustomer(id, customerData);
    dispatch({
      type: CUSTOMER_UPDATE_SUCCESS,
      payload: response.data
    });
    return response.data;
  } catch (error) {
    const errorMessage = getErrorMessage(error, 'Failed to update customer');
    dispatch({
      type: CUSTOMER_UPDATE_ERROR,
      payload: errorMessage
    });
    throw new Error(errorMessage);
  }
};

/**
 * Delete customer
 */
export const deleteCustomer = (id) => async (dispatch) => {
  dispatch({ type: CUSTOMER_DELETE_LOADING });
  try {
    await customersApi.deleteCustomer(id);
    dispatch({
      type: CUSTOMER_DELETE_SUCCESS,
      payload: id
    });
  } catch (error) {
    dispatch({
      type: CUSTOMER_DELETE_ERROR,
      payload: getErrorMessage(error, 'Failed to delete customer')
    });
  }
};

/**
 * Fetch customer orders
 */
export const fetchCustomerOrders = (customerId) => async (dispatch) => {
  dispatch({ type: CUSTOMER_ORDERS_LOADING });
  try {
    const response = await customersApi.getCustomerOrders(customerId);
    dispatch({
      type: CUSTOMER_ORDERS_SUCCESS,
      payload: response.data
    });
  } catch (error) {
    dispatch({
      type: CUSTOMER_ORDERS_ERROR,
      payload: getErrorMessage(error, 'Failed to fetch customer orders')
    });
  }
};

/**
 * Fetch customer statistics
 */
export const fetchCustomerStats = () => async (dispatch) => {
  dispatch({ type: CUSTOMER_STATS_LOADING });
  try {
    const response = await customersApi.getCustomerStats();
    dispatch({
      type: CUSTOMER_STATS_SUCCESS,
      payload: response.data
    });
  } catch (error) {
    dispatch({
      type: CUSTOMER_STATS_ERROR,
      payload: getErrorMessage(error, 'Failed to fetch customer statistics')
    });
  }
};

/**
 * Clear error state
 */
export const clearCustomerError = () => ({
  type: CLEAR_CUSTOMER_ERROR
});