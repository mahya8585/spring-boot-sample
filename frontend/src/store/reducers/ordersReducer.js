import {
  ORDERS_LOADING,
  ORDERS_SUCCESS,
  ORDERS_ERROR,
  ORDER_DETAIL_LOADING,
  ORDER_DETAIL_SUCCESS,
  ORDER_DETAIL_ERROR,
  ORDER_CREATE_LOADING,
  ORDER_CREATE_SUCCESS,
  ORDER_CREATE_ERROR,
  ORDER_UPDATE_LOADING,
  ORDER_UPDATE_SUCCESS,
  ORDER_UPDATE_ERROR,
  ORDER_STATUS_COUNTS_LOADING,
  ORDER_STATUS_COUNTS_SUCCESS,
  ORDER_STATUS_COUNTS_ERROR,
  CLEAR_ERROR,
} from '../actions/ordersActions';

const initialState = {
  orders: {
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: 0,
    size: 10,
  },
  selectedOrder: null,
  statusCounts: {
    pending: 0,
    confirmed: 0,
    picking: 0,
    shipped: 0,
    delivered: 0,
    cancelled: 0,
  },
  loading: false,
  detailLoading: false,
  createLoading: false,
  updateLoading: false,
  statusCountsLoading: false,
  error: null,
  detailError: null,
  createError: null,
  updateError: null,
  statusCountsError: null,
};

const ordersReducer = (state = initialState, action) => {
  switch (action.type) {
    case ORDERS_LOADING:
      return {
        ...state,
        loading: true,
        error: null,
      };
    case ORDERS_SUCCESS:
      return {
        ...state,
        loading: false,
        orders: action.payload,
        error: null,
      };
    case ORDERS_ERROR:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };
    
    case ORDER_DETAIL_LOADING:
      return {
        ...state,
        detailLoading: true,
        detailError: null,
      };
    case ORDER_DETAIL_SUCCESS:
      return {
        ...state,
        detailLoading: false,
        selectedOrder: action.payload,
        detailError: null,
      };
    case ORDER_DETAIL_ERROR:
      return {
        ...state,
        detailLoading: false,
        detailError: action.payload,
      };
    
    case ORDER_CREATE_LOADING:
      return {
        ...state,
        createLoading: true,
        createError: null,
      };
    case ORDER_CREATE_SUCCESS:
      return {
        ...state,
        createLoading: false,
        createError: null,
        // Optionally add the new order to the list
        orders: {
          ...state.orders,
          content: [action.payload, ...state.orders.content],
          totalElements: state.orders.totalElements + 1,
        },
      };
    case ORDER_CREATE_ERROR:
      return {
        ...state,
        createLoading: false,
        createError: action.payload,
      };
    
    case ORDER_UPDATE_LOADING:
      return {
        ...state,
        updateLoading: true,
        updateError: null,
      };
    case ORDER_UPDATE_SUCCESS:
      return {
        ...state,
        updateLoading: false,
        updateError: null,
        selectedOrder: action.payload,
        // Update the order in the list
        orders: {
          ...state.orders,
          content: state.orders.content.map(order =>
            order.id === action.payload.id ? action.payload : order
          ),
        },
      };
    case ORDER_UPDATE_ERROR:
      return {
        ...state,
        updateLoading: false,
        updateError: action.payload,
      };
    
    case ORDER_STATUS_COUNTS_LOADING:
      return {
        ...state,
        statusCountsLoading: true,
        statusCountsError: null,
      };
    case ORDER_STATUS_COUNTS_SUCCESS:
      return {
        ...state,
        statusCountsLoading: false,
        statusCounts: action.payload,
        statusCountsError: null,
      };
    case ORDER_STATUS_COUNTS_ERROR:
      return {
        ...state,
        statusCountsLoading: false,
        statusCountsError: action.payload,
      };
    
    case CLEAR_ERROR:
      return {
        ...state,
        error: null,
        detailError: null,
        createError: null,
        updateError: null,
        statusCountsError: null,
      };
    
    default:
      return state;
  }
};

export default ordersReducer;