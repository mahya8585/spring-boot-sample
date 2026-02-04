import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  TextField,
  Box,
  Typography,
  Chip,
  CircularProgress,
} from '@material-ui/core';
import { Alert } from '@material-ui/lab';
import { makeStyles } from '@material-ui/core/styles';
import { fetchBooks } from '../store/actions/booksActions';
import BookDetail from './BookDetail';
import { useI18n } from '../contexts/I18nContext';

const useStyles = makeStyles((theme) => ({
  root: {
    width: '100%',
  },
  paper: {
    width: '100%',
    marginBottom: theme.spacing(2),
  },
  table: {
    minWidth: 750,
    [theme.breakpoints.down('md')]: {
      minWidth: 'auto',
    },
  },
  searchBox: {
    marginBottom: theme.spacing(2),
    display: 'flex',
    gap: theme.spacing(2),
    alignItems: 'center',
    [theme.breakpoints.down('sm')]: {
      flexDirection: 'column',
      alignItems: 'stretch',
      gap: theme.spacing(1),
    },
  },
  levelChip: {
    minWidth: 60,
  },
  titleCell: {
    maxWidth: 200,
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
    [theme.breakpoints.down('sm')]: {
      maxWidth: 150,
    },
  },
  loading: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: 200,
  },
  mobileHidden: {
    [theme.breakpoints.down('sm')]: {
      display: 'none',
    },
  },
  compactCell: {
    [theme.breakpoints.down('sm')]: {
      padding: theme.spacing(1),
      fontSize: '0.75rem',
    },
  },
}));

function BookList() {
  const classes = useStyles();
  const dispatch = useDispatch();
  const { t } = useI18n();
  
  // Redux state
  const books = useSelector((state) => state.books?.books?.content || []);
  const loading = useSelector((state) => state.books?.loading || false);
  const error = useSelector((state) => state.books?.error);

  // Local state
  const [searchKeyword, setSearchKeyword] = useState('');
  const [detailDialogOpen, setDetailDialogOpen] = useState(false);
  const [selectedBookId, setSelectedBookId] = useState(null);
  
  // Constants for pagination and sorting (no UI controls)
  const page = 0;
  const rowsPerPage = 10;
  const sortBy = 'id';
  const sortDir = 'asc';

  // Fetch books on component mount and when search changes
  useEffect(() => {
    const params = {
      page,
      size: rowsPerPage,
      sortBy,
      sortDir,
      keyword: searchKeyword,
    };
    dispatch(fetchBooks(params));
  }, [dispatch, searchKeyword]); // Removed pagination dependencies since they're constants

  const handleSearchChange = (event) => {
    setSearchKeyword(event.target.value);
  };

  const handleSearchSubmit = (event) => {
    event.preventDefault();
    const params = {
      page,
      size: rowsPerPage,
      sortBy,
      sortDir,
      keyword: searchKeyword,
    };
    dispatch(fetchBooks(params));
  };

  const handleDetailClick = (bookId) => {
    setSelectedBookId(bookId);
    setDetailDialogOpen(true);
  };

  const handleDetailDialogClose = () => {
    setDetailDialogOpen(false);
    setSelectedBookId(null);
  };

  const getLevelColor = (level) => {
    switch (level) {
      case 'BEGINNER':
        return 'primary';
      case 'INTERMEDIATE':
        return 'secondary';
      case 'ADVANCED':
        return 'default';
      default:
        return 'default';
    }
  };

  const getLevelLabel = (level) => {
    switch (level) {
      case 'BEGINNER':
        return t('book.level.beginner', '初級');
      case 'INTERMEDIATE':
        return t('book.level.intermediate', '中級');
      case 'ADVANCED':
        return t('book.level.advanced', '上級');
      default:
        return level;
    }
  };

  if (loading && books.length === 0) {
    return (
      <div className={classes.loading}>
        <CircularProgress />
      </div>
    );
  }

  if (error) {
    return (
      <Box p={2}>
        <Alert severity="error" style={{ marginBottom: 16 }}>
          <Typography variant="h6" gutterBottom>
            {t('error.data.load.failed', 'データの読み込みに失敗しました')}
          </Typography>
          <Typography variant="body2" color="textSecondary">
            {error.includes('500') ? 
              t('error.server.connection', 'サーバーに接続できません。しばらく時間をおいてから再度お試しください。') :
              t('error.details', 'エラー詳細: {0}').replace('{0}', error)
            }
          </Typography>
          <Box mt={2}>
            <Button 
              variant="contained" 
              color="primary" 
              onClick={() => window.location.reload()}
            >
              {t('ui.reload', '再読み込み')}
            </Button>
          </Box>
        </Alert>
      </Box>
    );
  }

  return (
    <div className={classes.root}>
      <Typography variant="h4" gutterBottom>
        {t('ui.page.title.books', '書籍一覧')}
      </Typography>
      
      {/* Search */}
      <form onSubmit={handleSearchSubmit}>
        <Box className={classes.searchBox}>
          <TextField
            label={t('ui.search.keyword', '検索キーワード')}
            variant="outlined"
            value={searchKeyword}
            onChange={handleSearchChange}
            placeholder={t('ui.search.placeholder', 'タイトル、ISBN、著者名で検索')}
            style={{ minWidth: 300 }}
          />
          <Button
            type="submit"
            variant="contained"
            color="primary"
          >
            {t('ui.search', '検索')}
          </Button>
        </Box>
      </form>

      <Paper className={classes.paper}>
        <TableContainer>
          <Table className={classes.table}>
            <TableHead>
              <TableRow>
                <TableCell className={classes.compactCell}>{t('ui.table.header.id', 'ID')}</TableCell>
                <TableCell className={classes.mobileHidden}>ISBN-13</TableCell>
                <TableCell className={classes.compactCell}>{t('ui.table.header.title', 'タイトル')}</TableCell>
                <TableCell className={classes.mobileHidden}>{t('ui.table.header.publisher', '出版社')}</TableCell>
                <TableCell className={classes.mobileHidden}>{t('ui.table.header.publication.date', '発行日')}</TableCell>
                <TableCell className={classes.mobileHidden}>{t('ui.table.header.list.price', '定価')}</TableCell>
                <TableCell className={classes.compactCell}>{t('ui.table.header.level', 'レベル')}</TableCell>
                <TableCell className={classes.compactCell}>{t('actions', 'アクション')}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {books.map((book) => (
                <TableRow key={book.id}>
                  <TableCell className={classes.compactCell}>{book.id}</TableCell>
                  <TableCell className={classes.mobileHidden}>{book.isbn13}</TableCell>
                  <TableCell className={`${classes.titleCell} ${classes.compactCell}`}>
                    <Typography variant="body2" title={book.title}>
                      {book.title}
                    </Typography>
                  </TableCell>
                  <TableCell className={classes.mobileHidden}>{book.publisherName}</TableCell>
                  <TableCell className={classes.mobileHidden}>
                    {book.publicationDate ? new Date(book.publicationDate).toLocaleDateString('ja-JP') : '-'}
                  </TableCell>
                  <TableCell className={classes.mobileHidden}>
                    {book.listPrice ? `¥${book.listPrice.toLocaleString()}` : '-'}
                  </TableCell>
                  <TableCell className={classes.compactCell}>
                    {book.level && (
                      <Chip
                        label={getLevelLabel(book.level)}
                        color={getLevelColor(book.level)}
                        size="small"
                        className={classes.levelChip}
                      />
                    )}
                  </TableCell>
                  <TableCell className={classes.compactCell}>
                    <Button
                      variant="outlined"
                      size="small"
                      color="primary"
                      onClick={() => handleDetailClick(book.id)}
                    >
                      {t('ui.detail', '詳細')}
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>

      <BookDetail
        open={detailDialogOpen}
        onClose={handleDetailDialogClose}
        bookId={selectedBookId}
      />
    </div>
  );
}

export default BookList;