import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useHistory } from 'react-router-dom';
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
  Typography,
  Chip,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TablePagination,
  Grid,
  Card,
  CardContent,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  DialogContentText,
} from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import { 
  Add as AddIcon,
  Visibility as ViewIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Search as SearchIcon,
  Person as PersonIcon,
  Business as BusinessIcon,
} from '@material-ui/icons';
import { 
  fetchCustomers, 
  searchCustomers,
  deleteCustomer,
  fetchCustomerStats,
} from '../store/actions/customersActions';

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(3),
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: theme.spacing(3),
  },
  searchBox: {
    display: 'flex',
    gap: theme.spacing(2),
    marginBottom: theme.spacing(3),
    padding: theme.spacing(2),
  },
  statsCards: {
    marginBottom: theme.spacing(3),
  },
  table: {
    minWidth: 650,
  },
  statusChip: {
    minWidth: 80,
  },
  typeIcon: {
    marginRight: theme.spacing(1),
    verticalAlign: 'middle',
  },
  loadingContainer: {
    display: 'flex',
    justifyContent: 'center',
    padding: theme.spacing(4),
  },
  emptyState: {
    textAlign: 'center',
    padding: theme.spacing(4),
  },
  actionButtons: {
    display: 'flex',
    gap: theme.spacing(1),
  },
}));

const CustomerList = () => {
  const classes = useStyles();
  const dispatch = useDispatch();
  const history = useHistory();
  
  const {
    customers,
    customerStats,
    loading,
    statsLoading,
    deleteLoading,
    error,
  } = useSelector(state => state.customers);

  const [searchKeyword, setSearchKeyword] = useState('');
  const [customerTypeFilter, setCustomerTypeFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(20);
  const [deleteDialog, setDeleteDialog] = useState({ open: false, customer: null });

  const loadCustomers = useCallback(() => {
    const params = {
      page,
      size: rowsPerPage,
      sort: 'name,asc',
    };

    if (customerTypeFilter) params.customerType = customerTypeFilter;
    if (statusFilter) params.status = statusFilter;

    if (searchKeyword) {
      dispatch(searchCustomers(searchKeyword, params));
    } else {
      dispatch(fetchCustomers(params));
    }
  }, [dispatch, page, rowsPerPage, customerTypeFilter, statusFilter, searchKeyword]);

  useEffect(() => {
    loadCustomers();
    dispatch(fetchCustomerStats());
  }, [dispatch, page, rowsPerPage, loadCustomers]);

  useEffect(() => {
    if (error) {
      console.error('Customer error:', error);
    }
  }, [error]);

  const handleSearch = useCallback(() => {
    setPage(0);
    loadCustomers();
  }, [loadCustomers]);

  const handleClearSearch = useCallback(() => {
    setSearchKeyword('');
    setCustomerTypeFilter('');
    setStatusFilter('');
    setPage(0);
    dispatch(fetchCustomers({ page: 0, size: rowsPerPage, sort: 'name,asc' }));
  }, [dispatch, rowsPerPage]);

  const handleChangePage = useCallback((event, newPage) => {
    setPage(newPage);
  }, []);

  const handleChangeRowsPerPage = useCallback((event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  }, []);

  const handleViewCustomer = useCallback((customerId) => {
    history.push(`/customers/${customerId}`);
  }, [history]);

  const handleEditCustomer = useCallback((customerId) => {
    history.push(`/customers/${customerId}/edit`);
  }, [history]);

  const handleDeleteClick = useCallback((customer) => {
    setDeleteDialog({ open: true, customer });
  }, []);

  const handleDeleteConfirm = useCallback(async () => {
    if (deleteDialog.customer) {
      try {
        await dispatch(deleteCustomer(deleteDialog.customer.id));
        setDeleteDialog({ open: false, customer: null });
        loadCustomers(); // Reload the list
      } catch (error) {
        console.error('Failed to delete customer:', error);
        // Handle error appropriately - could show snackbar or alert
      }
    }
  }, [deleteDialog.customer, dispatch, loadCustomers]);

  const handleDeleteCancel = useCallback(() => {
    setDeleteDialog({ open: false, customer: null });
  }, []);

  const getStatusColor = useMemo(() => (status) => {
    switch (status) {
      case 'ACTIVE': return 'primary';
      case 'INACTIVE': return 'default';
      case 'DELETED': return 'secondary';
      default: return 'default';
    }
  }, []);

  const getCustomerTypeIcon = useMemo(() => (customerType) => {
    return customerType === 'INDIVIDUAL' ? 
      <PersonIcon className={classes.typeIcon} fontSize="small" /> : 
      <BusinessIcon className={classes.typeIcon} fontSize="small" />;
  }, [classes.typeIcon]);

  if (loading && customers.content.length === 0) {
    return (
      <div className={classes.loadingContainer}>
        <CircularProgress />
      </div>
    );
  }

  return (
    <div className={classes.root}>
      <div className={classes.header}>
        <Typography variant="h4" component="h1">
          顧客管理
        </Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={() => history.push('/customers/new')}
        >
          新規顧客追加
        </Button>
      </div>

      {/* Statistics Cards */}
      <Grid container spacing={3} className={classes.statsCards}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h6" component="h2">
                総顧客数
              </Typography>
              <Typography variant="h4" color="primary">
                {statsLoading ? <CircularProgress size={24} /> : customerStats.totalCustomers}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h6" component="h2">
                アクティブ
              </Typography>
              <Typography variant="h4" color="primary">
                {statsLoading ? <CircularProgress size={24} /> : customerStats.activeCustomers}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h6" component="h2">
                非アクティブ
              </Typography>
              <Typography variant="h4" style={{ color: '#ff9800' }}>
                {statsLoading ? <CircularProgress size={24} /> : customerStats.inactiveCustomers}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h6" component="h2">
                削除済み
              </Typography>
              <Typography variant="h4" style={{ color: '#f44336' }}>
                {statsLoading ? <CircularProgress size={24} /> : customerStats.deletedCustomers}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Search and Filter */}
      <Paper className={classes.searchBox}>
        <TextField
          label="検索キーワード"
          variant="outlined"
          size="small"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          placeholder="名前、メール、会社名で検索"
          style={{ minWidth: 250 }}
        />
        <FormControl variant="outlined" size="small" style={{ minWidth: 150 }}>
          <InputLabel>顧客タイプ</InputLabel>
          <Select
            value={customerTypeFilter}
            onChange={(e) => setCustomerTypeFilter(e.target.value)}
            label="顧客タイプ"
          >
            <MenuItem value="">すべて</MenuItem>
            <MenuItem value="INDIVIDUAL">個人</MenuItem>
            <MenuItem value="CORPORATE">法人</MenuItem>
          </Select>
        </FormControl>
        <FormControl variant="outlined" size="small" style={{ minWidth: 120 }}>
          <InputLabel>ステータス</InputLabel>
          <Select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            label="ステータス"
          >
            <MenuItem value="">すべて</MenuItem>
            <MenuItem value="ACTIVE">アクティブ</MenuItem>
            <MenuItem value="INACTIVE">非アクティブ</MenuItem>
          </Select>
        </FormControl>
        <Button
          variant="contained"
          color="primary"
          startIcon={<SearchIcon />}
          onClick={handleSearch}
        >
          検索
        </Button>
        <Button
          variant="outlined"
          onClick={handleClearSearch}
        >
          クリア
        </Button>
      </Paper>

      {/* Customers Table */}
      <Paper>
        <TableContainer>
          <Table className={classes.table}>
            <TableHead>
              <TableRow>
                <TableCell>顧客ID</TableCell>
                <TableCell>タイプ</TableCell>
                <TableCell>名前</TableCell>
                <TableCell>メールアドレス</TableCell>
                <TableCell>電話番号</TableCell>
                <TableCell>会社名</TableCell>
                <TableCell>ステータス</TableCell>
                <TableCell>登録日</TableCell>
                <TableCell>操作</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {customers.content.map((customer) => (
                <TableRow key={customer.id} hover>
                  <TableCell>{customer.id}</TableCell>
                  <TableCell>
                    {getCustomerTypeIcon(customer.customerType)}
                    {customer.customerType === 'INDIVIDUAL' ? '個人' : '法人'}
                  </TableCell>
                  <TableCell>
                    <Typography variant="subtitle2">{customer.name}</Typography>
                    {customer.nameKana && (
                      <Typography variant="caption" color="textSecondary">
                        {customer.nameKana}
                      </Typography>
                    )}
                  </TableCell>
                  <TableCell>{customer.email}</TableCell>
                  <TableCell>{customer.phone}</TableCell>
                  <TableCell>{customer.companyName || '-'}</TableCell>
                  <TableCell>
                    <Chip
                      label={customer.status === 'ACTIVE' ? 'アクティブ' : 
                             customer.status === 'INACTIVE' ? '非アクティブ' : '削除済み'}
                      color={getStatusColor(customer.status)}
                      size="small"
                      className={classes.statusChip}
                    />
                  </TableCell>
                  <TableCell>
                    {new Date(customer.createdAt).toLocaleDateString('ja-JP')}
                  </TableCell>
                  <TableCell>
                    <div className={classes.actionButtons}>
                      <IconButton 
                        size="small" 
                        onClick={() => handleViewCustomer(customer.id)}
                        title="詳細表示"
                      >
                        <ViewIcon />
                      </IconButton>
                      <IconButton 
                        size="small" 
                        onClick={() => handleEditCustomer(customer.id)}
                        title="編集"
                      >
                        <EditIcon />
                      </IconButton>
                      {customer.status !== 'DELETED' && (
                        <IconButton 
                          size="small" 
                          onClick={() => handleDeleteClick(customer)}
                          title="削除"
                        >
                          <DeleteIcon />
                        </IconButton>
                      )}
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>

        {customers.content.length === 0 && !loading && (
          <div className={classes.emptyState}>
            <Typography variant="body1" color="textSecondary">
              顧客が見つかりませんでした
            </Typography>
          </div>
        )}

        <TablePagination
          component="div"
          count={customers.totalElements}
          page={page}
          onPageChange={handleChangePage}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={handleChangeRowsPerPage}
          rowsPerPageOptions={[10, 20, 50]}
          labelRowsPerPage="表示件数:"
          labelDisplayedRows={({ from, to, count }) => 
            `${from}-${to} / ${count !== -1 ? count : `${to}以上`}`
          }
        />
      </Paper>

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={deleteDialog.open}
        onClose={handleDeleteCancel}
      >
        <DialogTitle>顧客の削除</DialogTitle>
        <DialogContent>
          <DialogContentText>
            顧客「{deleteDialog.customer?.name}」を削除しますか？
            この操作は論理削除され、後で復元することはできません。
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteCancel} color="primary">
            キャンセル
          </Button>
          <Button 
            onClick={handleDeleteConfirm} 
            color="secondary"
            disabled={deleteLoading}
          >
            {deleteLoading ? <CircularProgress size={20} /> : '削除'}
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default React.memo(CustomerList);