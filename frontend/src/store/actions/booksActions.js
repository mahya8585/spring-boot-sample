import { booksApi } from '../../services/api';

// Action Types
export const FETCH_BOOKS_START = 'FETCH_BOOKS_START';
export const FETCH_BOOKS_SUCCESS = 'FETCH_BOOKS_SUCCESS';
export const FETCH_BOOKS_FAILURE = 'FETCH_BOOKS_FAILURE';

export const FETCH_BOOK_START = 'FETCH_BOOK_START';
export const FETCH_BOOK_SUCCESS = 'FETCH_BOOK_SUCCESS';
export const FETCH_BOOK_FAILURE = 'FETCH_BOOK_FAILURE';

export const CREATE_BOOK_START = 'CREATE_BOOK_START';
export const CREATE_BOOK_SUCCESS = 'CREATE_BOOK_SUCCESS';
export const CREATE_BOOK_FAILURE = 'CREATE_BOOK_FAILURE';

export const UPDATE_BOOK_START = 'UPDATE_BOOK_START';
export const UPDATE_BOOK_SUCCESS = 'UPDATE_BOOK_SUCCESS';
export const UPDATE_BOOK_FAILURE = 'UPDATE_BOOK_FAILURE';

export const DELETE_BOOK_START = 'DELETE_BOOK_START';
export const DELETE_BOOK_SUCCESS = 'DELETE_BOOK_SUCCESS';
export const DELETE_BOOK_FAILURE = 'DELETE_BOOK_FAILURE';

export const FETCH_BOOK_DETAIL_START = 'FETCH_BOOK_DETAIL_START';
export const FETCH_BOOK_DETAIL_SUCCESS = 'FETCH_BOOK_DETAIL_SUCCESS';
export const FETCH_BOOK_DETAIL_FAILURE = 'FETCH_BOOK_DETAIL_FAILURE';

export const SET_EDIT_MODE = 'SET_EDIT_MODE';
export const CLEAR_BOOK_DETAIL = 'CLEAR_BOOK_DETAIL';

// Action Creators
export const fetchBooks = (params = {}) => {
  return async (dispatch) => {
    dispatch({ type: FETCH_BOOKS_START });
    
    try {
      const response = await booksApi.getBooks(params);
      dispatch({
        type: FETCH_BOOKS_SUCCESS,
        payload: response.data
      });
      return response.data;
    } catch (error) {
      dispatch({
        type: FETCH_BOOKS_FAILURE,
        payload: error.response?.data?.message || error.message
      });
      throw error;
    }
  };
};

export const fetchBook = (id) => {
  return async (dispatch) => {
    dispatch({ type: FETCH_BOOK_START });
    
    try {
      const response = await booksApi.getBook(id);
      dispatch({
        type: FETCH_BOOK_SUCCESS,
        payload: response.data
      });
      return response.data;
    } catch (error) {
      dispatch({
        type: FETCH_BOOK_FAILURE,
        payload: error.response?.data?.message || error.message
      });
      throw error;
    }
  };
};

export const createBook = (bookData) => {
  return async (dispatch) => {
    dispatch({ type: CREATE_BOOK_START });
    
    try {
      const response = await booksApi.createBook(bookData);
      dispatch({
        type: CREATE_BOOK_SUCCESS,
        payload: response.data
      });
      return response.data;
    } catch (error) {
      dispatch({
        type: CREATE_BOOK_FAILURE,
        payload: error.response?.data?.message || error.message
      });
      throw error;
    }
  };
};

export const updateBook = (id, bookData) => {
  return async (dispatch) => {
    dispatch({ type: UPDATE_BOOK_START });
    
    try {
      const response = await booksApi.updateBook(id, bookData);
      dispatch({
        type: UPDATE_BOOK_SUCCESS,
        payload: response.data
      });
      return response.data;
    } catch (error) {
      dispatch({
        type: UPDATE_BOOK_FAILURE,
        payload: error.response?.data?.message || error.message
      });
      throw error;
    }
  };
};

export const deleteBook = (id) => {
  return async (dispatch) => {
    dispatch({ type: DELETE_BOOK_START });
    
    try {
      await booksApi.deleteBook(id);
      dispatch({
        type: DELETE_BOOK_SUCCESS,
        payload: id
      });
      return id;
    } catch (error) {
      dispatch({
        type: DELETE_BOOK_FAILURE,
        payload: error.response?.data?.message || error.message
      });
      throw error;
    }
  };
};

export const fetchBookDetail = (id) => {
  return async (dispatch) => {
    dispatch({ type: FETCH_BOOK_DETAIL_START });
    
    try {
      const response = await booksApi.getBook(id);
      dispatch({
        type: FETCH_BOOK_DETAIL_SUCCESS,
        payload: response.data
      });
      return response.data;
    } catch (error) {
      dispatch({
        type: FETCH_BOOK_DETAIL_FAILURE,
        payload: error.response?.data?.message || error.message
      });
      throw error;
    }
  };
};

export const setEditMode = (isEditMode) => {
  return {
    type: SET_EDIT_MODE,
    payload: isEditMode
  };
};

export const clearBookDetail = () => {
  return {
    type: CLEAR_BOOK_DETAIL
  };
};