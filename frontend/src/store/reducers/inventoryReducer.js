import {
  FETCH_INVENTORY_START,
  FETCH_INVENTORY_SUCCESS,
  FETCH_INVENTORY_FAILURE,
  FETCH_INVENTORY_ALERTS_START,
  FETCH_INVENTORY_ALERTS_SUCCESS,
  FETCH_INVENTORY_ALERTS_FAILURE,
  RECEIVE_STOCK_START,
  RECEIVE_STOCK_SUCCESS,
  RECEIVE_STOCK_FAILURE,
  SELL_STOCK_START,
  SELL_STOCK_SUCCESS,
  SELL_STOCK_FAILURE,
} from '../actions/inventoryActions';

const initialState = {
  items: [],
  alerts: [],
  loading: false,
  error: null,
};

const inventoryReducer = (state = initialState, action) => {
  switch (action.type) {
    case FETCH_INVENTORY_START:
    case FETCH_INVENTORY_ALERTS_START:
    case RECEIVE_STOCK_START:
    case SELL_STOCK_START:
      return {
        ...state,
        loading: true,
        error: null,
      };

    case FETCH_INVENTORY_SUCCESS:
      return {
        ...state,
        loading: false,
        items: action.payload,
        error: null,
      };

    case FETCH_INVENTORY_ALERTS_SUCCESS:
      return {
        ...state,
        loading: false,
        alerts: action.payload,
        error: null,
      };

    case RECEIVE_STOCK_SUCCESS:
    case SELL_STOCK_SUCCESS:
      return {
        ...state,
        loading: false,
        items: state.items.map(item =>
          item.bookId === action.payload.bookId ? action.payload : item
        ),
        error: null,
      };

    case FETCH_INVENTORY_FAILURE:
    case FETCH_INVENTORY_ALERTS_FAILURE:
    case RECEIVE_STOCK_FAILURE:
    case SELL_STOCK_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };

    default:
      return state;
  }
};

export default inventoryReducer;