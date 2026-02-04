import { combineReducers } from 'redux';
import booksReducer from './booksReducer';
import inventoryReducer from './inventoryReducer';
import ordersReducer from './ordersReducer';
import customersReducer from './customersReducer';

const rootReducer = combineReducers({
  books: booksReducer,
  inventory: inventoryReducer,
  orders: ordersReducer,
  customers: customersReducer,
});

export default rootReducer;