import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Card,
  CardContent,
  Typography,
  Grid,
  Box,
  Button,
  Chip,
  CircularProgress,
  IconButton,
  Tab,
  Tabs,
} from '@material-ui/core';
import { Alert } from '@material-ui/lab';
import { makeStyles } from '@material-ui/core/styles';
import { 
  Close as CloseIcon,
  Edit as EditIcon,
  Book as BookIcon,
} from '@material-ui/icons';
import { 
  fetchBookDetail,
  clearBookDetail,
  setEditMode
} from '../store/actions/booksActions';
import BookEditForm from './BookEditForm';
import { useI18n } from '../contexts/I18nContext';

const useStyles = makeStyles((theme) => ({
  dialog: {
    '& .MuiDialog-paper': {
      maxWidth: '800px',
      width: '90%',
      maxHeight: '80vh',
      [theme.breakpoints.down('sm')]: {
        width: '95%',
        maxHeight: '90vh',
        margin: theme.spacing(1),
      },
    },
  },
  dialogTitle: {
    margin: 0,
    padding: theme.spacing(2),
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  closeButton: {
    color: theme.palette.grey[500],
  },
  content: {
    padding: theme.spacing(2),
    [theme.breakpoints.down('sm')]: {
      padding: theme.spacing(1),
    },
  },
  loading: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: '200px',
  },
  tabPanel: {
    padding: theme.spacing(2, 0),
    [theme.breakpoints.down('sm')]: {
      padding: theme.spacing(1, 0),
    },
  },
  infoCard: {
    marginBottom: theme.spacing(2),
    transition: 'box-shadow 0.3s ease-in-out',
    '&:hover': {
      boxShadow: theme.shadows[4],
    },
  },
  fieldLabel: {
    fontWeight: 'bold',
    color: theme.palette.text.secondary,
    marginBottom: theme.spacing(0.5),
  },
  fieldValue: {
    marginBottom: theme.spacing(2),
  },
  levelChip: {
    fontWeight: 'bold',
  },
  priceText: {
    fontSize: '1.2rem',
    fontWeight: 'bold',
    color: theme.palette.success.main,
  },
}));

const TabPanel = ({ children, value, index, ...other }) => {
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`book-tabpanel-${index}`}
      aria-labelledby={`book-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box>
          {children}
        </Box>
      )}
    </div>
  );
};

const BookDetail = ({ open, onClose, bookId }) => {
  const classes = useStyles();
  const dispatch = useDispatch();
  const { t } = useI18n();
  
  const {
    bookDetail,
    detailLoading,
    error,
    isEditMode,
  } = useSelector(state => state.books);

  const [currentTab, setCurrentTab] = useState(0);

  useEffect(() => {
    if (open && bookId) {
      dispatch(fetchBookDetail(bookId));
    }
    
    return () => {
      if (!open) {
        dispatch(clearBookDetail());
      }
    };
  }, [dispatch, open, bookId]);

  const handleTabChange = (event, newValue) => {
    setCurrentTab(newValue);
  };

  const handleEdit = () => {
    dispatch(setEditMode(true));
  };

  const handleClose = () => {
    dispatch(clearBookDetail());
    dispatch(setEditMode(false));
    setCurrentTab(0);
    onClose();
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

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('ja-JP');
  };

  const formatPrice = (price) => {
    if (!price) return '-';
    return `¥${price.toLocaleString()}`;
  };

  if (!open) {
    return null;
  }

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      className={classes.dialog}
      aria-labelledby="book-detail-dialog-title"
    >
      <DialogTitle id="book-detail-dialog-title" className={classes.dialogTitle}>
        <Box display="flex" alignItems="center">
          <BookIcon style={{ marginRight: 8 }} />
          <Typography variant="h6">
            {t('book.detail.dialog.title', '書籍詳細情報')}
          </Typography>
        </Box>
        <IconButton
          aria-label="close"
          className={classes.closeButton}
          onClick={handleClose}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent className={classes.content}>
        {detailLoading && (
          <div className={classes.loading}>
            <CircularProgress />
          </div>
        )}

        {error && (
          <Alert severity="error" style={{ marginBottom: 16 }}>
            {error}
          </Alert>
        )}

        {bookDetail && !detailLoading && (
          <>
            {isEditMode ? (
              <BookEditForm 
                book={bookDetail} 
                onCancel={() => dispatch(setEditMode(false))}
              />
            ) : (
              <>
                <Tabs
                  value={currentTab}
                  onChange={handleTabChange}
                  indicatorColor="primary"
                  textColor="primary"
                  variant="fullWidth"
                >
                  <Tab label={t('ui.tab.basic.info', '基本情報')} />
                  <Tab label={t('ui.tab.related.info', '関連情報')} />
                  <Tab label={t('ui.tab.inventory.info', '在庫情報')} />
                </Tabs>

            <TabPanel value={currentTab} index={0} className={classes.tabPanel}>
              <Card className={classes.infoCard}>
                <CardContent>
                  <Grid container spacing={3}>
                    <Grid item xs={12}>
                      <Typography className={classes.fieldLabel}>
                        {t('ui.field.title', 'タイトル')}
                      </Typography>
                      <Typography variant="h6" className={classes.fieldValue}>
                        {bookDetail.title}
                      </Typography>
                    </Grid>
                    
                    <Grid item xs={12} md={6}>
                      <Typography className={classes.fieldLabel}>
                        {t('ui.field.isbn13', 'ISBN-13')}
                      </Typography>
                      <Typography className={classes.fieldValue}>
                        {bookDetail.isbn13}
                      </Typography>
                    </Grid>
                    
                    <Grid item xs={12} md={6}>
                      <Typography className={classes.fieldLabel}>
                        {t('ui.field.publisher', '出版社')}
                      </Typography>
                      <Typography className={classes.fieldValue}>
                        {bookDetail.publisherName || '-'}
                      </Typography>
                    </Grid>
                    
                    <Grid item xs={12} md={6}>
                      <Typography className={classes.fieldLabel}>
                        {t('ui.field.publication.date', '発行日')}
                      </Typography>
                      <Typography className={classes.fieldValue}>
                        {formatDate(bookDetail.publicationDate)}
                      </Typography>
                    </Grid>
                    
                    <Grid item xs={12} md={6}>
                      <Typography className={classes.fieldLabel}>
                        {t('ui.field.edition', '版次')}
                      </Typography>
                      <Typography className={classes.fieldValue}>
                        {bookDetail.edition || '-'}
                      </Typography>
                    </Grid>
                    
                    <Grid item xs={12} md={6}>
                      <Typography className={classes.fieldLabel}>
                        {t('ui.field.pages', 'ページ数')}
                      </Typography>
                      <Typography className={classes.fieldValue}>
                        {bookDetail.pages ? `${bookDetail.pages}${t('ui.field.suffix.pages', 'ページ')}` : '-'}
                      </Typography>
                    </Grid>
                    
                    <Grid item xs={12} md={6}>
                      <Typography className={classes.fieldLabel}>
                        {t('ui.field.level', '技術レベル')}
                      </Typography>
                      <div className={classes.fieldValue}>
                        {bookDetail.level ? (
                          <Chip
                            label={getLevelLabel(bookDetail.level)}
                            color={getLevelColor(bookDetail.level)}
                            size="small"
                            className={classes.levelChip}
                          />
                        ) : '-'}
                      </div>
                    </Grid>
                    
                    <Grid item xs={12} md={6}>
                      <Typography className={classes.fieldLabel}>
                        {t('ui.field.list.price', '定価')}
                      </Typography>
                      <Typography className={classes.priceText}>
                        {formatPrice(bookDetail.listPrice)}
                      </Typography>
                    </Grid>
                    
                    <Grid item xs={12} md={6}>
                      <Typography className={classes.fieldLabel}>
                        {t('ui.field.selling.price', '販売価格')}
                      </Typography>
                      <Typography className={classes.priceText}>
                        {formatPrice(bookDetail.sellingPrice)}
                      </Typography>
                    </Grid>
                    
                    {bookDetail.versionInfo && (
                      <Grid item xs={12} md={6}>
                        <Typography className={classes.fieldLabel}>
                          {t('book.version.info.label', 'バージョン情報')}
                        </Typography>
                        <Typography className={classes.fieldValue}>
                          {bookDetail.versionInfo}
                        </Typography>
                      </Grid>
                    )}
                    
                    {bookDetail.sampleCodeUrl && (
                      <Grid item xs={12} md={6}>
                        <Typography className={classes.fieldLabel}>
                          {t('book.sample.code.url.label', 'サンプルコードURL')}
                        </Typography>
                        <Typography className={classes.fieldValue}>
                          <a 
                            href={bookDetail.sampleCodeUrl} 
                            target="_blank" 
                            rel="noopener noreferrer"
                            style={{ color: '#1976d2', textDecoration: 'none' }}
                          >
                            {bookDetail.sampleCodeUrl}
                          </a>
                        </Typography>
                      </Grid>
                    )}
                    
                    {bookDetail.description && (
                      <Grid item xs={12}>
                        <Typography className={classes.fieldLabel}>
                          {t('book.description.label', '説明')}
                        </Typography>
                        <Typography className={classes.fieldValue}>
                          {bookDetail.description}
                        </Typography>
                      </Grid>
                    )}
                  </Grid>
                </CardContent>
              </Card>
            </TabPanel>

            <TabPanel value={currentTab} index={1} className={classes.tabPanel}>
              <Card className={classes.infoCard}>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {t('future.author.info', '著者情報')}
                  </Typography>
                  <Typography color="textSecondary" paragraph>
                    {t('future.author.info.description', '著者情報の表示機能は Phase 2 で実装予定です。')}
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    {t('future.author.info.future', '将来的に以下の情報が表示されます：')}
                  </Typography>
                  <ul style={{ color: '#757575', fontSize: '0.875rem' }}>
                    <li>{t('future.author.profile', '著者名・プロフィール')}</li>
                    <li>{t('future.author.other.books', '著者の他の書籍')}</li>
                    <li>{t('future.author.experience', '経歴・専門分野')}</li>
                  </ul>
                </CardContent>
              </Card>
              
              <Card className={classes.infoCard}>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {t('future.tech.category', '技術カテゴリ')}
                  </Typography>
                  <Typography color="textSecondary" paragraph>
                    {t('future.tech.category.description', 'カテゴリ情報の表示機能は Phase 2 で実装予定です。')}
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    {t('future.tech.category.future', '将来的に以下の情報が表示されます：')}
                  </Typography>
                  <ul style={{ color: '#757575', fontSize: '0.875rem' }}>
                    <li>{t('future.tech.category.fields', '技術分野（プログラミング言語、フレームワーク等）')}</li>
                    <li>{t('future.tech.category.keywords', '関連技術キーワード')}</li>
                    <li>{t('future.tech.category.related', '同カテゴリの関連書籍')}</li>
                  </ul>
                </CardContent>
              </Card>
            </TabPanel>

            <TabPanel value={currentTab} index={2} className={classes.tabPanel}>
              <Card className={classes.infoCard}>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {t('future.inventory.status', '在庫状況')}
                  </Typography>
                  <Typography color="textSecondary" paragraph>
                    {t('future.inventory.status.description', '在庫情報の表示機能は Phase 2 で実装予定です。')}
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    {t('future.inventory.status.future', '将来的に以下の情報が表示されます：')}
                  </Typography>
                  <ul style={{ color: '#757575', fontSize: '0.875rem' }}>
                    <li>{t('future.inventory.store.stock', '店頭在庫数')}</li>
                    <li>{t('future.inventory.warehouse.stock', '倉庫在庫数')}</li>
                    <li>{t('future.inventory.total.stock', '総在庫数')}</li>
                    <li>{t('future.inventory.reserved.orders', '予約注文数')}</li>
                    <li>{t('future.inventory.planned.orders', '発注予定数')}</li>
                    <li>{t('future.inventory.alerts', '在庫アラート設定')}</li>
                  </ul>
                </CardContent>
              </Card>
              
              <Card className={classes.infoCard}>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {t('future.sales.performance', '販売実績')}
                  </Typography>
                  <Typography color="textSecondary" paragraph>
                    {t('future.sales.performance.description', '販売実績の表示機能は Phase 2 で実装予定です。')}
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    {t('future.sales.performance.future', '将来的に以下の情報が表示されます：')}
                  </Typography>
                  <ul style={{ color: '#757575', fontSize: '0.875rem' }}>
                    <li>{t('future.sales.monthly.quantity', '月別売上数量')}</li>
                    <li>{t('future.sales.trend.graph', '売上推移グラフ')}</li>
                    <li>{t('future.sales.popularity.ranking', '人気ランキング')}</li>
                  </ul>
                </CardContent>
              </Card>
            </TabPanel>
              </>
            )}
          </>
        )}
      </DialogContent>

      {bookDetail && !detailLoading && !isEditMode && (
        <DialogActions>
          <Button onClick={handleClose} color="default">
            {t('ui.close', '閉じる')}
          </Button>
          <Button
            onClick={handleEdit}
            color="primary"
            variant="contained"
            startIcon={<EditIcon />}
          >
            {t('ui.edit', '編集')}
          </Button>
        </DialogActions>
      )}
    </Dialog>
  );
};

export default BookDetail;