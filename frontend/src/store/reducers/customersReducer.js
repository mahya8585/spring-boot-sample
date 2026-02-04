import {
  CUSTOMERS_LOADING,
  CUSTOMERS_SUCCESS,
  CUSTOMERS_ERROR,
  CUSTOMER_DETAIL_LOADING,
  CUSTOMER_DETAIL_SUCCESS,
  CUSTOMER_DETAIL_ERROR,
  CUSTOMER_CREATE_LOADING,
  CUSTOMER_CREATE_SUCCESS,
  CUSTOMER_CREATE_ERROR,
  CUSTOMER_UPDATE_LOADING,
  CUSTOMER_UPDATE_SUCCESS,
  CUSTOMER_UPDATE_ERROR,
  CUSTOMER_DELETE_LOADING,
  CUSTOMER_DELETE_SUCCESS,
  CUSTOMER_DELETE_ERROR,
  CUSTOMER_ORDERS_LOADING,
  CUSTOMER_ORDERS_SUCCESS,
  CUSTOMER_ORDERS_ERROR,
  CUSTOMER_STATS_LOADING,
  CUSTOMER_STATS_SUCCESS,
  CUSTOMER_STATS_ERROR,
  CLEAR_CUSTOMER_ERROR,
} from '../actions/customersActions';

const initialState = {
  customers: {
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: 0,
    size: 20,
    first: true,
    last: true,
    empty: true,
  },
  selectedCustomer: null,
  customerOrders: [],
  customerStats: {
    totalCustomers: 0,
    activeCustomers: 0,
    inactiveCustomers: 0,
    deletedCustomers: 0,
  },
  loading: false,
  detailLoading: false,
  createLoading: false,
  updateLoading: false,
  deleteLoading: false,
  ordersLoading: false,
  statsLoading: false,
  error: null,
  detailError: null,
  createError: null,
  updateError: null,
  deleteError: null,
  ordersError: null,
  statsError: null,
};

const customersReducer = (state = initialState, action) => {
  switch (action.type) {
    // Customers list actions
    case CUSTOMERS_LOADING:
      return {
        ...state,
        loading: true,
        error: null,
      };

    case CUSTOMERS_SUCCESS:
      return {
        ...state,
        loading: false,
        customers: action.payload,
        error: null,
      };

    case CUSTOMERS_ERROR:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };

    // Customer detail actions
    case CUSTOMER_DETAIL_LOADING:
      return {
        ...state,
        detailLoading: true,
        detailError: null,
      };

    case CUSTOMER_DETAIL_SUCCESS:
      return {
        ...state,
        detailLoading: false,
        selectedCustomer: action.payload,
        detailError: null,
      };

    case CUSTOMER_DETAIL_ERROR:
      return {
        ...state,
        detailLoading: false,
        detailError: action.payload,
      };

    // Customer create actions
    case CUSTOMER_CREATE_LOADING:
      return {
        ...state,
        createLoading: true,
        createError: null,
      };

    case CUSTOMER_CREATE_SUCCESS:
      return {
        ...state,
        createLoading: false,
        customers: {
          ...state.customers,
          content: [action.payload, ...state.customers.content],
          totalElements: state.customers.totalElements + 1,
        },
        createError: null,
      };

    case CUSTOMER_CREATE_ERROR:
      return {
        ...state,
        createLoading: false,
        createError: action.payload,
      };

    // Customer update actions
    case CUSTOMER_UPDATE_LOADING:
      return {
        ...state,
        updateLoading: true,
        updateError: null,
      };

    case CUSTOMER_UPDATE_SUCCESS:
      return {
        ...state,
        updateLoading: false,
        selectedCustomer: action.payload,
        customers: {
          ...state.customers,
          content: state.customers.content.map(customer =>
            customer.id === action.payload.id ? action.payload : customer
          ),
        },
        updateError: null,
      };

    case CUSTOMER_UPDATE_ERROR:
      return {
        ...state,
        updateLoading: false,
        updateError: action.payload,
      };

    // Customer delete actions
    case CUSTOMER_DELETE_LOADING:
      return {
        ...state,
        deleteLoading: true,
        deleteError: null,
      };

    case CUSTOMER_DELETE_SUCCESS:
      return {
        ...state,
        deleteLoading: false,
        customers: {
          ...state.customers,
          content: state.customers.content.filter(customer => customer.id !== action.payload),
          totalElements: state.customers.totalElements - 1,
        },
        deleteError: null,
      };

    case CUSTOMER_DELETE_ERROR:
      return {
        ...state,
        deleteLoading: false,
        deleteError: action.payload,
      };

    // Customer orders actions
    case CUSTOMER_ORDERS_LOADING:
      return {
        ...state,
        ordersLoading: true,
        ordersError: null,
      };

    case CUSTOMER_ORDERS_SUCCESS:
      return {
        ...state,
        ordersLoading: false,
        customerOrders: action.payload,
        ordersError: null,
      };

    case CUSTOMER_ORDERS_ERROR:
      return {
        ...state,
        ordersLoading: false,
        ordersError: action.payload,
      };

    // Customer stats actions
    case CUSTOMER_STATS_LOADING:
      return {
        ...state,
        statsLoading: true,
        statsError: null,
      };

    case CUSTOMER_STATS_SUCCESS:
      return {
        ...state,
        statsLoading: false,
        customerStats: action.payload,
        statsError: null,
      };

    case CUSTOMER_STATS_ERROR:
      return {
        ...state,
        statsLoading: false,
        statsError: action.payload,
      };

    // Clear error
    case CLEAR_CUSTOMER_ERROR:
      return {
        ...state,
        error: null,
        detailError: null,
        createError: null,
        updateError: null,
        deleteError: null,
        ordersError: null,
        statsError: null,
      };

    default:
      return state;
  }
};

export default customersReducer;