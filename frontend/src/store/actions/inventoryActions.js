import { inventoryApi } from '../../services/api';

// Action Types
export const FETCH_INVENTORY_START = 'FETCH_INVENTORY_START';
export const FETCH_INVENTORY_SUCCESS = 'FETCH_INVENTORY_SUCCESS';
export const FETCH_INVENTORY_FAILURE = 'FETCH_INVENTORY_FAILURE';

export const FETCH_INVENTORY_ALERTS_START = 'FETCH_INVENTORY_ALERTS_START';
export const FETCH_INVENTORY_ALERTS_SUCCESS = 'FETCH_INVENTORY_ALERTS_SUCCESS';
export const FETCH_INVENTORY_ALERTS_FAILURE = 'FETCH_INVENTORY_ALERTS_FAILURE';

export const RECEIVE_STOCK_START = 'RECEIVE_STOCK_START';
export const RECEIVE_STOCK_SUCCESS = 'RECEIVE_STOCK_SUCCESS';
export const RECEIVE_STOCK_FAILURE = 'RECEIVE_STOCK_FAILURE';

export const SELL_STOCK_START = 'SELL_STOCK_START';
export const SELL_STOCK_SUCCESS = 'SELL_STOCK_SUCCESS';
export const SELL_STOCK_FAILURE = 'SELL_STOCK_FAILURE';

// Action Creators
export const fetchInventory = () => {
  return async (dispatch) => {
    dispatch({ type: FETCH_INVENTORY_START });
    
    try {
      const response = await inventoryApi.getInventory();
      dispatch({
        type: FETCH_INVENTORY_SUCCESS,
        payload: response.data
      });
      return response.data;
    } catch (error) {
      dispatch({
        type: FETCH_INVENTORY_FAILURE,
        payload: error.response?.data?.message || error.message
      });
      throw error;
    }
  };
};

export const fetchInventoryAlerts = () => {
  return async (dispatch) => {
    dispatch({ type: FETCH_INVENTORY_ALERTS_START });
    
    try {
      const response = await inventoryApi.getInventoryAlerts();
      dispatch({
        type: FETCH_INVENTORY_ALERTS_SUCCESS,
        payload: response.data
      });
      return response.data;
    } catch (error) {
      dispatch({
        type: FETCH_INVENTORY_ALERTS_FAILURE,
        payload: error.response?.data?.message || error.message
      });
      throw error;
    }
  };
};

export const receiveStock = (stockData) => {
  return async (dispatch) => {
    dispatch({ type: RECEIVE_STOCK_START });
    
    try {
      const response = await inventoryApi.receiveStock(stockData);
      dispatch({
        type: RECEIVE_STOCK_SUCCESS,
        payload: response.data
      });
      return response.data;
    } catch (error) {
      dispatch({
        type: RECEIVE_STOCK_FAILURE,
        payload: error.response?.data?.message || error.message
      });
      throw error;
    }
  };
};

export const sellStock = (stockData) => {
  return async (dispatch) => {
    dispatch({ type: SELL_STOCK_START });
    
    try {
      const response = await inventoryApi.sellStock(stockData);
      dispatch({
        type: SELL_STOCK_SUCCESS,
        payload: response.data
      });
      return response.data;
    } catch (error) {
      dispatch({
        type: SELL_STOCK_FAILURE,
        payload: error.response?.data?.message || error.message
      });
      throw error;
    }
  };
};