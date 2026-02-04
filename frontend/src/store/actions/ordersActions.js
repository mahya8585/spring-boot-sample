import { ordersApi } from '../../services/api';

// Action types
export const ORDERS_LOADING = 'ORDERS_LOADING';
export const ORDERS_SUCCESS = 'ORDERS_SUCCESS';
export const ORDERS_ERROR = 'ORDERS_ERROR';
export const ORDER_DETAIL_LOADING = 'ORDER_DETAIL_LOADING';
export const ORDER_DETAIL_SUCCESS = 'ORDER_DETAIL_SUCCESS';
export const ORDER_DETAIL_ERROR = 'ORDER_DETAIL_ERROR';
export const ORDER_CREATE_LOADING = 'ORDER_CREATE_LOADING';
export const ORDER_CREATE_SUCCESS = 'ORDER_CREATE_SUCCESS';
export const ORDER_CREATE_ERROR = 'ORDER_CREATE_ERROR';
export const ORDER_UPDATE_LOADING = 'ORDER_UPDATE_LOADING';
export const ORDER_UPDATE_SUCCESS = 'ORDER_UPDATE_SUCCESS';
export const ORDER_UPDATE_ERROR = 'ORDER_UPDATE_ERROR';
export const ORDER_STATUS_COUNTS_LOADING = 'ORDER_STATUS_COUNTS_LOADING';
export const ORDER_STATUS_COUNTS_SUCCESS = 'ORDER_STATUS_COUNTS_SUCCESS';
export const ORDER_STATUS_COUNTS_ERROR = 'ORDER_STATUS_COUNTS_ERROR';
export const CLEAR_ERROR = 'CLEAR_ERROR';

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
  if (error.message) {
    return error.message;
  }
  return defaultMessage;
};

// Action creators
export const clearError = () => ({
  type: CLEAR_ERROR
});

export const fetchOrders = (params = {}) => {
  return async (dispatch) => {
    dispatch({ type: ORDERS_LOADING });
    try {
      const response = await ordersApi.getOrders(params);
      dispatch({
        type: ORDERS_SUCCESS,
        payload: response.data
      });
    } catch (error) {
      const errorMessage = getErrorMessage(error, '注文一覧の取得に失敗しました');
      dispatch({
        type: ORDERS_ERROR,
        payload: errorMessage
      });
    }
  };
};

export const fetchOrderById = (id) => {
  return async (dispatch) => {
    dispatch({ type: ORDER_DETAIL_LOADING });
    try {
      const response = await ordersApi.getOrder(id);
      dispatch({
        type: ORDER_DETAIL_SUCCESS,
        payload: response.data
      });
    } catch (error) {
      const errorMessage = getErrorMessage(error, '注文詳細の取得に失敗しました');
      dispatch({
        type: ORDER_DETAIL_ERROR,
        payload: errorMessage
      });
    }
  };
};

export const fetchOrderByNumber = (orderNumber) => {
  return async (dispatch) => {
    dispatch({ type: ORDER_DETAIL_LOADING });
    try {
      const response = await ordersApi.getOrderByNumber(orderNumber);
      dispatch({
        type: ORDER_DETAIL_SUCCESS,
        payload: response.data
      });
    } catch (error) {
      const errorMessage = getErrorMessage(error, '注文詳細の取得に失敗しました');
      dispatch({
        type: ORDER_DETAIL_ERROR,
        payload: errorMessage
      });
    }
  };
};

export const createOrder = (orderData) => {
  return async (dispatch) => {
    dispatch({ type: ORDER_CREATE_LOADING });
    try {
      const response = await ordersApi.createOrder(orderData);
      dispatch({
        type: ORDER_CREATE_SUCCESS,
        payload: response.data
      });
      return response.data;
    } catch (error) {
      const errorMessage = getErrorMessage(error, '注文の作成に失敗しました');
      dispatch({
        type: ORDER_CREATE_ERROR,
        payload: errorMessage
      });
      throw error;
    }
  };
};

export const confirmOrder = (id) => {
  return async (dispatch) => {
    dispatch({ type: ORDER_UPDATE_LOADING });
    try {
      const response = await ordersApi.confirmOrder(id);
      dispatch({
        type: ORDER_UPDATE_SUCCESS,
        payload: response.data
      });
      return response.data;
    } catch (error) {
      const errorMessage = getErrorMessage(error, '注文の確認に失敗しました');
      dispatch({
        type: ORDER_UPDATE_ERROR,
        payload: errorMessage
      });
      throw error;
    }
  };
};

export const pickOrder = (id) => {
  return async (dispatch) => {
    dispatch({ type: ORDER_UPDATE_LOADING });
    try {
      const response = await ordersApi.pickOrder(id);
      dispatch({
        type: ORDER_UPDATE_SUCCESS,
        payload: response.data
      });
      return response.data;
    } catch (error) {
      const errorMessage = getErrorMessage(error, '注文の梱包開始に失敗しました');
      dispatch({
        type: ORDER_UPDATE_ERROR,
        payload: errorMessage
      });
      throw error;
    }
  };
};

export const shipOrder = (id) => {
  return async (dispatch) => {
    dispatch({ type: ORDER_UPDATE_LOADING });
    try {
      const response = await ordersApi.shipOrder(id);
      dispatch({
        type: ORDER_UPDATE_SUCCESS,
        payload: response.data
      });
      return response.data;
    } catch (error) {
      const errorMessage = getErrorMessage(error, '注文の出荷に失敗しました');
      dispatch({
        type: ORDER_UPDATE_ERROR,
        payload: errorMessage
      });
      throw error;
    }
  };
};

export const deliverOrder = (id) => {
  return async (dispatch) => {
    dispatch({ type: ORDER_UPDATE_LOADING });
    try {
      const response = await ordersApi.deliverOrder(id);
      dispatch({
        type: ORDER_UPDATE_SUCCESS,
        payload: response.data
      });
      return response.data;
    } catch (error) {
      const errorMessage = getErrorMessage(error, '注文の配達完了に失敗しました');
      dispatch({
        type: ORDER_UPDATE_ERROR,
        payload: errorMessage
      });
      throw error;
    }
  };
};

export const fetchOrderStatusCounts = () => {
  return async (dispatch) => {
    dispatch({ type: ORDER_STATUS_COUNTS_LOADING });
    try {
      const response = await ordersApi.getStatusCounts();
      dispatch({
        type: ORDER_STATUS_COUNTS_SUCCESS,
        payload: response.data
      });
    } catch (error) {
      const errorMessage = getErrorMessage(error, 'ステータス集計の取得に失敗しました');
      dispatch({
        type: ORDER_STATUS_COUNTS_ERROR,
        payload: errorMessage
      });
    }
  };
};