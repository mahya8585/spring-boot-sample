import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams, useHistory } from 'react-router-dom';
import {
  Card,
  CardContent,
  Typography,
  Grid,
  Box,
  Button,
  Chip,
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Tab,
  Tabs,
} from '@material-ui/core';
import { Alert } from '@material-ui/lab';
import { makeStyles } from '@material-ui/core/styles';
import { 
  Edit as EditIcon,
  ArrowBack as BackIcon,
  Person as PersonIcon,
  Business as BusinessIcon,
  Email as EmailIcon,
  Phone as PhoneIcon,
  Home as HomeIcon,
  Work as WorkIcon,
  CalendarToday as CalendarIcon,
} from '@material-ui/icons';
import { 
  fetchCustomerById,
  fetchCustomerOrders,
  clearCustomerError
} from '../store/actions/customersActions';
import { useI18n } from '../contexts/I18nContext';

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
  backButton: {
    marginRight: theme.spacing(2),
  },
  customerCard: {
    marginBottom: theme.spacing(3),
  },
  infoGrid: {
    marginTop: theme.spacing(2),
  },
  infoItem: {
    display: 'flex',
    alignItems: 'center',
    marginBottom: theme.spacing(1),
  },
  infoIcon: {
    marginRight: theme.spacing(1),
    color: theme.palette.text.secondary,
  },
  statusChip: {
    marginLeft: theme.spacing(1),
  },
  sectionTitle: {
    marginTop: theme.spacing(3),
    marginBottom: theme.spacing(2),
  },
  loadingContainer: {
    display: 'flex',
    justifyContent: 'center',
    padding: theme.spacing(4),
  },
  tabContent: {
    paddingTop: theme.spacing(2),
  },
  orderTable: {
    marginTop: theme.spacing(2),
  },
  emptyState: {
    textAlign: 'center',
    padding: theme.spacing(3),
    color: theme.palette.text.secondary,
  },
}));

const CustomerDetail = () => {
  const classes = useStyles();
  const dispatch = useDispatch();
  const history = useHistory();
  const { id } = useParams();
  const { t } = useI18n();
  
  const {
    selectedCustomer: customer,
    customerOrders,
    detailLoading,
    ordersLoading,
    detailError,
    ordersError,
  } = useSelector(state => state.customers);

  const [currentTab, setCurrentTab] = useState(0);

  useEffect(() => {
    if (id) {
      dispatch(fetchCustomerById(id));
      dispatch(fetchCustomerOrders(id));
    }
    
    return () => {
      dispatch(clearCustomerError());
    };
  }, [dispatch, id]);

  const handleTabChange = (event, newValue) => {
    setCurrentTab(newValue);
  };

  const handleEdit = () => {
    history.push(`/customers/${id}/edit`);
  };

  const handleBack = () => {
    history.push('/customers');
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE': return 'primary';
      case 'INACTIVE': return 'default';
      case 'DELETED': return 'secondary';
      default: return 'default';
    }
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case 'ACTIVE': return 'アクティブ';
      case 'INACTIVE': return '非アクティブ';
      case 'DELETED': return '削除済み';
      default: return status;
    }
  };

  const getCustomerTypeLabel = (customerType) => {
    return customerType === 'INDIVIDUAL' ? '個人' : '法人';
  };

  const getCustomerTypeIcon = (customerType) => {
    return customerType === 'INDIVIDUAL' ? 
      <PersonIcon className={classes.infoIcon} /> : 
      <BusinessIcon className={classes.infoIcon} />;
  };

  const getOrderStatusLabel = (status) => {
    const statusMap = {
      'PENDING': '保留中',
      'CONFIRMED': '確認済み',
      'PICKING': 'ピッキング中',
      'SHIPPED': '発送済み',
      'DELIVERED': '配達完了',
      'CANCELLED': 'キャンセル',
    };
    return statusMap[status] || status;
  };

  const getOrderStatusColor = (status) => {
    switch (status) {
      case 'PENDING': return 'default';
      case 'CONFIRMED': return 'primary';
      case 'PICKING': return 'secondary';
      case 'SHIPPED': return 'info';
      case 'DELIVERED': return 'success';
      case 'CANCELLED': return 'error';
      default: return 'default';
    }
  };

  if (detailLoading) {
    return (
      <div className={classes.loadingContainer}>
        <CircularProgress />
      </div>
    );
  }

  if (detailError) {
    return (
      <div className={classes.root}>
        <Alert severity="error" style={{ marginBottom: 16 }}>
          {detailError}
        </Alert>
        <Button onClick={handleBack}>戻る</Button>
      </div>
    );
  }

  if (!customer) {
    return (
      <div className={classes.root}>
        <Typography variant="h6">顧客が見つかりません</Typography>
        <Button onClick={handleBack}>戻る</Button>
      </div>
    );
  }

  return (
    <div className={classes.root}>
      <div className={classes.header}>
        <Box display="flex" alignItems="center">
          <Button
            variant="outlined"
            startIcon={<BackIcon />}
            onClick={handleBack}
            className={classes.backButton}
          >
            戻る
          </Button>
          <Typography variant="h4" component="h1">
            顧客詳細
          </Typography>
        </Box>
        <Button
          variant="contained"
          color="primary"
          startIcon={<EditIcon />}
          onClick={handleEdit}
          disabled={customer.status === 'DELETED'}
        >
          編集
        </Button>
      </div>

      <Card className={classes.customerCard}>
        <CardContent>
          <Box display="flex" alignItems="center" marginBottom={2}>
            {getCustomerTypeIcon(customer.customerType)}
            <Typography variant="h5" component="h2" style={{ marginRight: 16 }}>
              {customer.name}
            </Typography>
            <Chip
              label={getCustomerTypeLabel(customer.customerType)}
              variant="outlined"
              size="small"
            />
            <Chip
              label={getStatusLabel(customer.status)}
              color={getStatusColor(customer.status)}
              size="small"
              className={classes.statusChip}
            />
          </Box>
          
          {customer.nameKana && (
            <Typography variant="subtitle1" color="textSecondary" gutterBottom>
              {customer.nameKana}
            </Typography>
          )}

          <Grid container spacing={3} className={classes.infoGrid}>
            <Grid item xs={12} md={6}>
              <Typography variant="h6" gutterBottom>{t('customer.basic.info', '基本情報')}</Typography>
              
              <div className={classes.infoItem}>
                <EmailIcon className={classes.infoIcon} />
                <Typography>{customer.email}</Typography>
              </div>
              
              <div className={classes.infoItem}>
                <PhoneIcon className={classes.infoIcon} />
                <Typography>{customer.phone}</Typography>
              </div>
              
              {customer.birthDate && (
                <div className={classes.infoItem}>
                  <CalendarIcon className={classes.infoIcon} />
                  <Typography>
                    {t('customer.birthdate', '生年月日')}: {new Date(customer.birthDate).toLocaleDateString('ja-JP')}
                  </Typography>
                </div>
              )}
              
              {customer.gender && (
                <div className={classes.infoItem}>
                  <PersonIcon className={classes.infoIcon} />
                  <Typography>
                    {t('customer.gender', '性別')}: {customer.gender === 'MALE' ? t('customer.gender.male', '男性') : 
                            customer.gender === 'FEMALE' ? t('customer.gender.female', '女性') : t('customer.gender.other', 'その他')}
                  </Typography>
                </div>
              )}
            </Grid>

            <Grid item xs={12} md={6}>
              <Typography variant="h6" gutterBottom>{t('customer.occupation.info', '職業・会社情報')}</Typography>
              
              {customer.occupation && (
                <div className={classes.infoItem}>
                  <WorkIcon className={classes.infoIcon} />
                  <Typography>{t('customer.occupation', '職業')}: {customer.occupation}</Typography>
                </div>
              )}
              
              {customer.companyName && (
                <div className={classes.infoItem}>
                  <BusinessIcon className={classes.infoIcon} />
                  <Typography>{t('customer.company', '会社名')}: {customer.companyName}</Typography>
                </div>
              )}
              
              {customer.department && (
                <div className={classes.infoItem}>
                  <WorkIcon className={classes.infoIcon} />
                  <Typography>{t('customer.department', '部署')}: {customer.department}</Typography>
                </div>
              )}
              
              {(customer.postalCode || customer.address) && (
                <div className={classes.infoItem}>
                  <HomeIcon className={classes.infoIcon} />
                  <Typography>
                    住所: {customer.postalCode && `〒${customer.postalCode} `}
                    {customer.address}
                  </Typography>
                </div>
              )}
            </Grid>
          </Grid>

          {customer.notes && (
            <Box marginTop={2}>
              <Typography variant="h6" gutterBottom>備考</Typography>
              <Typography variant="body2" color="textSecondary">
                {customer.notes}
              </Typography>
            </Box>
          )}

          <Box marginTop={2}>
            <Typography variant="caption" color="textSecondary">
              登録日: {new Date(customer.createdAt).toLocaleString('ja-JP')}
              {customer.updatedAt !== customer.createdAt && (
                <> | 更新日: {new Date(customer.updatedAt).toLocaleString('ja-JP')}</>
              )}
            </Typography>
          </Box>
        </CardContent>
      </Card>

      <Paper>
        <Tabs
          value={currentTab}
          onChange={handleTabChange}
          indicatorColor="primary"
          textColor="primary"
        >
          <Tab label="購買履歴" />
          <Tab label="統計情報" />
        </Tabs>

        <div className={classes.tabContent}>
          {currentTab === 0 && (
            <div>
              <Typography variant="h6" className={classes.sectionTitle}>
                購買履歴 ({customerOrders.length}件)
              </Typography>
              
              {ordersLoading ? (
                <div className={classes.loadingContainer}>
                  <CircularProgress />
                </div>
              ) : ordersError ? (
                <Alert severity="error">{ordersError}</Alert>
              ) : customerOrders.length > 0 ? (
                <TableContainer className={classes.orderTable}>
                  <Table>
                    <TableHead>
                      <TableRow>
                        <TableCell>注文番号</TableCell>
                        <TableCell>注文日</TableCell>
                        <TableCell>ステータス</TableCell>
                        <TableCell>支払方法</TableCell>
                        <TableCell align="right">金額</TableCell>
                        <TableCell>備考</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {customerOrders.map((order) => (
                        <TableRow key={order.id} hover>
                          <TableCell>
                            <Button
                              color="primary"
                              onClick={() => history.push(`/orders/${order.id}`)}
                            >
                              {order.orderNumber}
                            </Button>
                          </TableCell>
                          <TableCell>
                            {new Date(order.orderDate).toLocaleDateString('ja-JP')}
                          </TableCell>
                          <TableCell>
                            <Chip
                              label={getOrderStatusLabel(order.status)}
                              color={getOrderStatusColor(order.status)}
                              size="small"
                            />
                          </TableCell>
                          <TableCell>{order.paymentMethod}</TableCell>
                          <TableCell align="right">
                            ¥{order.totalAmount.toLocaleString()}
                          </TableCell>
                          <TableCell>{order.notes || '-'}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              ) : (
                <div className={classes.emptyState}>
                  <Typography>購買履歴がありません</Typography>
                </div>
              )}
            </div>
          )}

          {currentTab === 1 && (
            <div>
              <Typography variant="h6" className={classes.sectionTitle}>
                統計情報
              </Typography>
              
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6} md={3}>
                  <Card>
                    <CardContent>
                      <Typography variant="h6">注文回数</Typography>
                      <Typography variant="h4" color="primary">
                        {customerOrders.length}
                      </Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <Card>
                    <CardContent>
                      <Typography variant="h6">総購入金額</Typography>
                      <Typography variant="h4" color="primary">
                        ¥{customerOrders.reduce((sum, order) => sum + order.totalAmount, 0).toLocaleString()}
                      </Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <Card>
                    <CardContent>
                      <Typography variant="h6">平均注文金額</Typography>
                      <Typography variant="h4" color="primary">
                        ¥{customerOrders.length > 0 ? 
                          Math.round(customerOrders.reduce((sum, order) => sum + order.totalAmount, 0) / customerOrders.length).toLocaleString() : 
                          0}
                      </Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <Card>
                    <CardContent>
                      <Typography variant="h6">最終注文日</Typography>
                      <Typography variant="h6" color="primary">
                        {customerOrders.length > 0 ?
                          new Date(Math.max(...customerOrders.map(o => new Date(o.orderDate)))).toLocaleDateString('ja-JP') :
                          '-'
                        }
                      </Typography>
                    </CardContent>
                  </Card>
                </Grid>
              </Grid>
            </div>
          )}
        </div>
      </Paper>
    </div>
  );
};

export default CustomerDetail;