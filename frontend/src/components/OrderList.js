import React, { useEffect, useState, useCallback } from 'react';
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
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TablePagination,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  Card,
  CardContent,
} from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import { 
  Add as AddIcon,
  Visibility as ViewIcon,
  CheckCircle as ConfirmIcon,
  LocalShipping as ShipIcon,
  Assignment as PickIcon,
  DoneAll as DeliverIcon,
} from '@material-ui/icons';
import { 
  fetchOrders, 
  fetchOrderById,
  confirmOrder,
  pickOrder,
  shipOrder,
  deliverOrder,
  fetchOrderStatusCounts 
} from '../store/actions/ordersActions';
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
  },
  searchBox: {
    marginBottom: theme.spacing(2),
    display: 'flex',
    gap: theme.spacing(2),
    alignItems: 'center',
    flexWrap: 'wrap',
  },
  statusChip: {
    minWidth: 80,
  },
  orderNumberCell: {
    fontWeight: 'bold',
    color: theme.palette.primary.main,
  },
  amountCell: {
    fontWeight: 'bold',
    textAlign: 'right',
  },
  loading: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: 400,
  },
  statusCard: {
    margin: theme.spacing(1),
    textAlign: 'center',
  },
  statusTitle: {
    fontSize: '2rem',
    fontWeight: 'bold',
    color: theme.palette.primary.main,
  },
  actionButton: {
    margin: theme.spacing(0.5),
  },
}));

const statusColors = {
  PENDING: 'default',
  CONFIRMED: 'primary',
  PICKING: 'secondary',
  SHIPPED: 'info',
  DELIVERED: 'success',
  CANCELLED: 'error',
};

const statusLabels = {
  PENDING: 'order.status.pending',
  CONFIRMED: 'order.status.confirmed',
  PICKING: 'order.status.picking',
  SHIPPED: 'order.status.shipped',
  DELIVERED: 'order.status.delivered',
  CANCELLED: 'order.status.cancelled',
};

const typeLabels = {
  WALK_IN: 'order.type.walk.in',
  ONLINE: 'order.type.online',
  PHONE: 'order.type.phone',
};

function OrderList() {
  const classes = useStyles();
  const dispatch = useDispatch();
  const { t, locale } = useI18n();
  const { 
    orders, 
    selectedOrder,
    statusCounts,
    loading, 
    detailLoading,
    updateLoading,
  } = useSelector(state => state.orders);

  const [searchKeyword, setSearchKeyword] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [typeFilter, setTypeFilter] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [sortBy] = useState('orderDate');
  const [sortDir] = useState('desc');
  const [detailDialogOpen, setDetailDialogOpen] = useState(false);

  const loadOrders = useCallback(() => {
    const params = {
      page,
      size: rowsPerPage,
      sortBy,
      sortDir,
      keyword: searchKeyword,
    };
    
    if (statusFilter) params.status = statusFilter;
    if (typeFilter) params.type = typeFilter;
    
    dispatch(fetchOrders(params));
  }, [dispatch, page, rowsPerPage, sortBy, sortDir, searchKeyword, statusFilter, typeFilter]);

  useEffect(() => {
    loadOrders();
    dispatch(fetchOrderStatusCounts());
  }, [dispatch, page, rowsPerPage, sortBy, sortDir, statusFilter, typeFilter, loadOrders]);

  const handleSearch = () => {
    setPage(0);
    loadOrders();
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleViewOrder = (orderId) => {
    dispatch(fetchOrderById(orderId));
    setDetailDialogOpen(true);
  };

  const handleStatusUpdate = async (orderId, action) => {
    try {
      switch (action) {
        case 'confirm':
          await dispatch(confirmOrder(orderId));
          break;
        case 'pick':
          await dispatch(pickOrder(orderId));
          break;
        case 'ship':
          await dispatch(shipOrder(orderId));
          break;
        case 'deliver':
          await dispatch(deliverOrder(orderId));
          break;
        default:
          console.warn('Unknown action:', action);
      }
      loadOrders();
      dispatch(fetchOrderStatusCounts());
    } catch (error) {
      console.error('Failed to update order status:', error);
    }
  };

  const renderStatusActions = (order) => {
    const actions = [];
    
    switch (order.status) {
      case 'PENDING':
        actions.push(
          <Button
            key="confirm"
            size="small"
            variant="contained"
            color="primary"
            startIcon={<ConfirmIcon />}
            className={classes.actionButton}
            onClick={() => handleStatusUpdate(order.id, 'confirm')}
            disabled={updateLoading}
          >
            {t('order.action.confirm', '確認')}
          </Button>
        );
        break;
      case 'CONFIRMED':
        actions.push(
          <Button
            key="pick"
            size="small"
            variant="contained"
            color="secondary"
            startIcon={<PickIcon />}
            className={classes.actionButton}
            onClick={() => handleStatusUpdate(order.id, 'pick')}
            disabled={updateLoading}
          >
            {t('order.action.pick', '梱包')}
          </Button>
        );
        break;
      case 'PICKING':
        actions.push(
          <Button
            key="ship"
            size="small"
            variant="contained"
            startIcon={<ShipIcon />}
            className={classes.actionButton}
            onClick={() => handleStatusUpdate(order.id, 'ship')}
            disabled={updateLoading}
          >
            {t('order.action.ship', '出荷')}
          </Button>
        );
        break;
      case 'SHIPPED':
        actions.push(
          <Button
            key="deliver"
            size="small"
            variant="contained"
            color="primary"
            startIcon={<DeliverIcon />}
            className={classes.actionButton}
            onClick={() => handleStatusUpdate(order.id, 'deliver')}
            disabled={updateLoading}
          >
            {t('order.action.deliver', '配達完了')}
          </Button>
        );
        break;
      default:
        // No actions for other statuses
        break;
    }

    actions.push(
      <Button
        key="view"
        size="small"
        variant="outlined"
        startIcon={<ViewIcon />}
        className={classes.actionButton}
        onClick={() => handleViewOrder(order.id)}
      >
        {t('ui.detail', '詳細')}
      </Button>
    );

    return actions;
  };

  const formatCurrency = (amount) => {
    const localeCode = locale === 'en' ? 'en-US' : 'ja-JP';
    const currency = locale === 'en' ? 'USD' : 'JPY';
    return new Intl.NumberFormat(localeCode, {
      style: 'currency',
      currency: currency,
    }).format(amount);
  };

  const formatDateTime = (dateTimeString) => {
    const localeCode = locale === 'en' ? 'en-US' : 'ja-JP';
    return new Date(dateTimeString).toLocaleString(localeCode);
  };

  if (loading) {
    return (
      <div className={classes.loading}>
        <CircularProgress />
      </div>
    );
  }

  return (
    <div className={classes.root}>
      <Typography variant="h4" gutterBottom>
        {t('order.management', '注文管理')}
      </Typography>

      {/* Status Summary Cards */}
      <Grid container spacing={2} style={{ marginBottom: 16 }}>
        {Object.entries(statusCounts).map(([status, count]) => (
          <Grid item xs={12} sm={6} md={2} key={status}>
            <Card className={classes.statusCard}>
              <CardContent>
                <Typography variant="h6">
                  {t(statusLabels[status.toUpperCase()], status)}
                </Typography>
                <Typography className={classes.statusTitle}>
                  {count}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Search and Filters */}
      <Box className={classes.searchBox}>
        <TextField
          label="検索（注文番号、顧客ID）"
          variant="outlined"
          size="small"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          style={{ minWidth: 200 }}
        />
        
        <FormControl variant="outlined" size="small" style={{ minWidth: 120 }}>
          <InputLabel>ステータス</InputLabel>
          <Select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            label="ステータス"
          >
            <MenuItem value="">すべて</MenuItem>
            {Object.entries(statusLabels).map(([value, label]) => (
              <MenuItem key={value} value={value}>{t(label, value)}</MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl variant="outlined" size="small" style={{ minWidth: 120 }}>
          <InputLabel>注文タイプ</InputLabel>
          <Select
            value={typeFilter}
            onChange={(e) => setTypeFilter(e.target.value)}
            label="注文タイプ"
          >
            <MenuItem value="">すべて</MenuItem>
            {Object.entries(typeLabels).map(([value, label]) => (
              <MenuItem key={value} value={value}>{t(label, value)}</MenuItem>
            ))}
          </Select>
        </FormControl>

        <Button
          variant="contained"
          color="primary"
          onClick={handleSearch}
        >
          検索
        </Button>

        <Button
          variant="contained"
          color="secondary"
          startIcon={<AddIcon />}
          onClick={() => {/* Navigate to order form */}}
        >
          新規注文
        </Button>
      </Box>

      {/* Orders Table */}
      <Paper className={classes.paper}>
        <TableContainer>
          <Table className={classes.table}>
            <TableHead>
              <TableRow>
                <TableCell>注文番号</TableCell>
                <TableCell>注文日時</TableCell>
                <TableCell>顧客ID</TableCell>
                <TableCell>タイプ</TableCell>
                <TableCell>ステータス</TableCell>
                <TableCell align="right">金額</TableCell>
                <TableCell>アクション</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {orders.content.map((order) => (
                <TableRow key={order.id}>
                  <TableCell className={classes.orderNumberCell}>
                    {order.orderNumber}
                  </TableCell>
                  <TableCell>
                    {formatDateTime(order.orderDate)}
                  </TableCell>
                  <TableCell>
                    {order.customerId || '-'}
                  </TableCell>
                  <TableCell>
                    {t(typeLabels[order.type], order.type)}
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={t(statusLabels[order.status], order.status)}
                      color={statusColors[order.status] || 'default'}
                      size="small"
                      className={classes.statusChip}
                    />
                  </TableCell>
                  <TableCell className={classes.amountCell}>
                    {formatCurrency(order.totalAmount)}
                  </TableCell>
                  <TableCell>
                    {renderStatusActions(order)}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>

        <TablePagination
          rowsPerPageOptions={[5, 10, 25]}
          component="div"
          count={orders.totalElements}
          rowsPerPage={rowsPerPage}
          page={page}
          onChangePage={handleChangePage}
          onChangeRowsPerPage={handleChangeRowsPerPage}
          labelRowsPerPage="ページあたりの行数:"
          labelDisplayedRows={({ from, to, count }) => 
            `${from}-${to} / ${count !== -1 ? count : `more than ${to}`}`
          }
        />
      </Paper>

      {/* Order Detail Dialog */}
      <Dialog
        open={detailDialogOpen}
        onClose={() => setDetailDialogOpen(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>{t('order.details', '注文詳細')}</DialogTitle>
        <DialogContent>
          {detailLoading ? (
            <CircularProgress />
          ) : selectedOrder ? (
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <Typography variant="h6">基本情報</Typography>
                <Typography>注文番号: {selectedOrder.orderNumber}</Typography>
                <Typography>注文日時: {formatDateTime(selectedOrder.orderDate)}</Typography>
                <Typography>顧客ID: {selectedOrder.customerId || '-'}</Typography>
                <Typography>注文タイプ: {t(typeLabels[selectedOrder.type], selectedOrder.type)}</Typography>
                <Typography>支払い方法: {selectedOrder.paymentMethod}</Typography>
                <Typography>
                  ステータス: 
                  <Chip
                    label={t(statusLabels[selectedOrder.status], selectedOrder.status)}
                    color={statusColors[selectedOrder.status]}
                    size="small"
                    style={{ marginLeft: 8 }}
                  />
                </Typography>
                <Typography>合計金額: {formatCurrency(selectedOrder.totalAmount)}</Typography>
                {selectedOrder.notes && (
                  <Typography>備考: {selectedOrder.notes}</Typography>
                )}
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography variant="h6">進捗情報</Typography>
                {selectedOrder.confirmedDate && (
                  <Typography>確認日時: {formatDateTime(selectedOrder.confirmedDate)}</Typography>
                )}
                {selectedOrder.shippedDate && (
                  <Typography>出荷日時: {formatDateTime(selectedOrder.shippedDate)}</Typography>
                )}
                {selectedOrder.deliveredDate && (
                  <Typography>配達日時: {formatDateTime(selectedOrder.deliveredDate)}</Typography>
                )}
              </Grid>
              <Grid item xs={12}>
                <Typography variant="h6">注文商品</Typography>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>書籍名</TableCell>
                      <TableCell>ISBN</TableCell>
                      <TableCell align="right">数量</TableCell>
                      <TableCell align="right">単価</TableCell>
                      <TableCell align="right">小計</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {selectedOrder.orderItems?.map((item) => (
                      <TableRow key={item.id}>
                        <TableCell>{item.bookTitle}</TableCell>
                        <TableCell>{item.bookIsbn13}</TableCell>
                        <TableCell align="right">{item.quantity}</TableCell>
                        <TableCell align="right">{formatCurrency(item.unitPrice)}</TableCell>
                        <TableCell align="right">{formatCurrency(item.totalPrice)}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </Grid>
            </Grid>
          ) : null}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDetailDialogOpen(false)} color="primary">
            閉じる
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

export default OrderList;