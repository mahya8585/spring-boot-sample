import {
  FETCH_BOOKS_START,
  FETCH_BOOKS_SUCCESS,
  FETCH_BOOKS_FAILURE,
  FETCH_BOOK_START,
  FETCH_BOOK_SUCCESS,
  FETCH_BOOK_FAILURE,
  CREATE_BOOK_START,
  CREATE_BOOK_SUCCESS,
  CREATE_BOOK_FAILURE,
  UPDATE_BOOK_START,
  UPDATE_BOOK_SUCCESS,
  UPDATE_BOOK_FAILURE,
  DELETE_BOOK_START,
  DELETE_BOOK_SUCCESS,
  DELETE_BOOK_FAILURE,
  FETCH_BOOK_DETAIL_START,
  FETCH_BOOK_DETAIL_SUCCESS,
  FETCH_BOOK_DETAIL_FAILURE,
  SET_EDIT_MODE,
  CLEAR_BOOK_DETAIL,
} from '../actions/booksActions';

const initialState = {
  books: [],
  selectedBook: null,
  bookDetail: null,
  loading: false,
  detailLoading: false,
  error: null,
  isEditMode: false,
};

const booksReducer = (state = initialState, action) => {
  switch (action.type) {
    case FETCH_BOOKS_START:
    case FETCH_BOOK_START:
    case CREATE_BOOK_START:
    case UPDATE_BOOK_START:
    case DELETE_BOOK_START:
      return {
        ...state,
        loading: true,
        error: null,
      };

    case FETCH_BOOK_DETAIL_START:
      return {
        ...state,
        detailLoading: true,
        error: null,
      };

    case FETCH_BOOKS_SUCCESS:
      return {
        ...state,
        loading: false,
        books: action.payload,
        error: null,
      };

    case FETCH_BOOK_SUCCESS:
      return {
        ...state,
        loading: false,
        selectedBook: action.payload,
        error: null,
      };

    case FETCH_BOOK_DETAIL_SUCCESS:
      return {
        ...state,
        detailLoading: false,
        bookDetail: action.payload,
        error: null,
      };

    case CREATE_BOOK_SUCCESS:
      return {
        ...state,
        loading: false,
        books: {
          ...state.books,
          content: [action.payload, ...(state.books.content || [])],
          totalElements: (state.books.totalElements || 0) + 1,
        },
        error: null,
      };

    case UPDATE_BOOK_SUCCESS:
      return {
        ...state,
        loading: false,
        books: {
          ...state.books,
          content: (state.books.content || []).map(book =>
            book.id === action.payload.id ? action.payload : book
          ),
        },
        selectedBook: action.payload,
        bookDetail: state.bookDetail?.id === action.payload.id ? action.payload : state.bookDetail,
        error: null,
      };

    case DELETE_BOOK_SUCCESS:
      return {
        ...state,
        loading: false,
        books: {
          ...state.books,
          content: (state.books.content || []).filter(book => book.id !== action.payload),
          totalElements: Math.max((state.books.totalElements || 0) - 1, 0),
        },
        error: null,
      };

    case FETCH_BOOKS_FAILURE:
    case FETCH_BOOK_FAILURE:
    case CREATE_BOOK_FAILURE:
    case UPDATE_BOOK_FAILURE:
    case DELETE_BOOK_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };

    case FETCH_BOOK_DETAIL_FAILURE:
      return {
        ...state,
        detailLoading: false,
        error: action.payload,
      };

    case SET_EDIT_MODE:
      return {
        ...state,
        isEditMode: action.payload,
      };

    case CLEAR_BOOK_DETAIL:
      return {
        ...state,
        bookDetail: null,
        isEditMode: false,
        detailLoading: false,
        error: null,
      };

    default:
      return state;
  }
};

export default booksReducer;