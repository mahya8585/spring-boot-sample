import React, { useState, useEffect, useCallback } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Typography,
  Button,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Box,
  Grid,
  Chip,
  List,
  ListItem,
  ListItemText,
  Alert,
  CircularProgress,
  Tabs,
  Tab,
  IconButton,
} from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import {
  Close as CloseIcon,
  SwapHoriz as TransferIcon,
  Block as ReserveIcon,
  History as HistoryIcon,
  Scanner as ScannerIcon,
} from '@material-ui/icons';
import { inventoryApi } from '../services/api';

const useStyles = makeStyles((theme) => ({
  dialog: {
    '& .MuiDialog-paper': {
      maxWidth: '900px',
      width: '90%',
      maxHeight: '80vh',
    },
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: theme.spacing(2),
  },
  formControl: {
    minWidth: 120,
    marginBottom: theme.spacing(2),
  },
  chip: {
    margin: theme.spacing(0.5),
  },
  tabPanel: {
    padding: theme.spacing(2, 0),
  },
  loading: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: '200px',
  },
  transactionItem: {
    borderBottom: `1px solid ${theme.palette.divider}`,
    '&:last-child': {
      borderBottom: 'none',
    },
  },
  actionButtons: {
    display: 'flex',
    gap: theme.spacing(1),
    marginBottom: theme.spacing(2),
  },
}));

function TabPanel({ children, value, index, ...other }) {
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`inventory-tabpanel-${index}`}
      aria-labelledby={`inventory-tab-${index}`}
      {...other}
    >
      {value === index && <Box>{children}</Box>}
    </div>
  );
}

const AdvancedInventoryManagement = ({ open, onClose, inventory }) => {
  const classes = useStyles();
  const [currentTab, setCurrentTab] = useState(0);
  const [loading, setLoading] = useState(false);
  const [transactions, setTransactions] = useState([]);
  const [reservations, setReservations] = useState([]);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Transfer form state
  const [transferForm, setTransferForm] = useState({
    transferType: 'STORE_TO_WAREHOUSE',
    quantity: '',
    reason: '',
  });

  // Reservation form state
  const [reservationForm, setReservationForm] = useState({
    quantity: '',
    reservationType: 'MANUAL',
    orderId: '',
    customerId: '',
    reservedHours: '24',
  });

  // Barcode scanner state
  const [barcodeForm, setBarcodeForm] = useState({
    barcode: '',
    operation: 'LOOKUP',
  });

  const loadTransactionHistory = useCallback(async () => {
    if (!inventory?.id) return;
    try {
      setLoading(true);
      const response = await inventoryApi.getTransactionHistory(inventory.id);
      setTransactions(response.data);
    } catch (error) {
      setError('取引履歴の読み込みに失敗しました');
    } finally {
      setLoading(false);
    }
  }, [inventory?.id]);

  const loadActiveReservations = useCallback(async () => {
    if (!inventory?.id) return;
    try {
      const response = await inventoryApi.getActiveReservations(inventory.id);
      setReservations(response.data);
    } catch (error) {
      setError('予約情報の読み込みに失敗しました');
    }
  }, [inventory?.id]);

  useEffect(() => {
    if (open && inventory) {
      loadTransactionHistory();
      loadActiveReservations();
    }
  }, [open, inventory, loadTransactionHistory, loadActiveReservations]);

  const handleTransfer = async () => {
    try {
      setLoading(true);
      setError('');
      
      const transferData = {
        inventoryId: inventory.id,
        transferType: transferForm.transferType,
        quantity: parseInt(transferForm.quantity),
        reason: transferForm.reason,
      };

      const response = await inventoryApi.transferStock(transferData);
      
      if (response.data.success) {
        setSuccess('在庫移動が完了しました');
        setTransferForm({ transferType: 'STORE_TO_WAREHOUSE', quantity: '', reason: '' });
        loadTransactionHistory();
      } else {
        setError(response.data.message || '在庫移動に失敗しました');
      }
    } catch (error) {
      setError('在庫移動に失敗しました: ' + (error.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  const handleReservation = async () => {
    try {
      setLoading(true);
      setError('');

      const reservedUntil = new Date();
      reservedUntil.setHours(reservedUntil.getHours() + parseInt(reservationForm.reservedHours));

      const reservationData = {
        inventoryId: inventory.id,
        quantity: parseInt(reservationForm.quantity),
        reservationType: reservationForm.reservationType,
        orderId: reservationForm.orderId ? parseInt(reservationForm.orderId) : null,
        customerId: reservationForm.customerId ? parseInt(reservationForm.customerId) : null,
        reservedUntil: reservedUntil.toISOString(),
      };

      const response = await inventoryApi.createReservation(reservationData);
      
      if (response.data.success) {
        setSuccess('在庫予約が作成されました');
        setReservationForm({ quantity: '', reservationType: 'MANUAL', orderId: '', customerId: '', reservedHours: '24' });
        loadActiveReservations();
      } else {
        setError(response.data.message || '在庫予約に失敗しました');
      }
    } catch (error) {
      setError('在庫予約に失敗しました: ' + (error.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  const handleReleaseReservation = async (reservationId) => {
    try {
      setLoading(true);
      await inventoryApi.releaseReservation(reservationId);
      setSuccess('予約を解除しました');
      loadActiveReservations();
    } catch (error) {
      setError('予約解除に失敗しました');
    } finally {
      setLoading(false);
    }
  };

  const handleBarcodeProcess = async () => {
    try {
      setLoading(true);
      setError('');

      const barcodeData = {
        barcode: barcodeForm.barcode,
        operation: barcodeForm.operation,
      };

      await inventoryApi.processBarcodeScanned(barcodeData);
      setSuccess('バーコード処理が完了しました');
      setBarcodeForm({ barcode: '', operation: 'LOOKUP' });
    } catch (error) {
      setError('バーコード処理に失敗しました（機能は未実装です）');
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = (event, newValue) => {
    setCurrentTab(newValue);
    setError('');
    setSuccess('');
  };

  const handleClose = () => {
    setCurrentTab(0);
    setError('');
    setSuccess('');
    onClose();
  };

  if (!inventory) return null;

  return (
    <Dialog open={open} onClose={handleClose} className={classes.dialog}>
      <DialogTitle>
        <Box className={classes.header}>
          <Typography variant="h6">
            高度な在庫管理 - {inventory.bookTitle || inventory.book?.title}
          </Typography>
          <IconButton onClick={handleClose}>
            <CloseIcon />
          </IconButton>
        </Box>
      </DialogTitle>

      <DialogContent>
        {error && <Alert severity="error" style={{ marginBottom: 16 }}>{error}</Alert>}
        {success && <Alert severity="success" style={{ marginBottom: 16 }}>{success}</Alert>}

        <Tabs value={currentTab} onChange={handleTabChange} aria-label="inventory management tabs">
          <Tab label="在庫移動" icon={<TransferIcon />} />
          <Tab label="在庫予約" icon={<ReserveIcon />} />
          <Tab label="取引履歴" icon={<HistoryIcon />} />
          <Tab label="バーコード" icon={<ScannerIcon />} />
        </Tabs>

        {/* Stock Transfer Tab */}
        <TabPanel value={currentTab} index={0} className={classes.tabPanel}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <Typography variant="h6">在庫移動</Typography>
              <Typography variant="body2" color="textSecondary" gutterBottom>
                店頭と倉庫間での在庫移動を行います
              </Typography>
            </Grid>
            
            <Grid item xs={12} md={6}>
              <FormControl fullWidth className={classes.formControl}>
                <InputLabel>移動方向</InputLabel>
                <Select
                  value={transferForm.transferType}
                  onChange={(e) => setTransferForm({...transferForm, transferType: e.target.value})}
                >
                  <MenuItem value="STORE_TO_WAREHOUSE">店頭 → 倉庫</MenuItem>
                  <MenuItem value="WAREHOUSE_TO_STORE">倉庫 → 店頭</MenuItem>
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="移動数量"
                type="number"
                value={transferForm.quantity}
                onChange={(e) => setTransferForm({...transferForm, quantity: e.target.value})}
                className={classes.formControl}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                fullWidth
                label="移動理由"
                multiline
                rows={2}
                value={transferForm.reason}
                onChange={(e) => setTransferForm({...transferForm, reason: e.target.value})}
                className={classes.formControl}
              />
            </Grid>

            <Grid item xs={12}>
              <Button
                variant="contained"
                color="primary"
                onClick={handleTransfer}
                disabled={loading || !transferForm.quantity}
                startIcon={loading ? <CircularProgress size={20} /> : <TransferIcon />}
              >
                在庫移動実行
              </Button>
            </Grid>
          </Grid>
        </TabPanel>

        {/* Stock Reservation Tab */}
        <TabPanel value={currentTab} index={1} className={classes.tabPanel}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <Typography variant="h6">在庫予約</Typography>
              <Typography variant="body2" color="textSecondary" gutterBottom>
                在庫の予約・引当を行います
              </Typography>
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="予約数量"
                type="number"
                value={reservationForm.quantity}
                onChange={(e) => setReservationForm({...reservationForm, quantity: e.target.value})}
                className={classes.formControl}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <FormControl fullWidth className={classes.formControl}>
                <InputLabel>予約タイプ</InputLabel>
                <Select
                  value={reservationForm.reservationType}
                  onChange={(e) => setReservationForm({...reservationForm, reservationType: e.target.value})}
                >
                  <MenuItem value="MANUAL">手動予約</MenuItem>
                  <MenuItem value="ORDER">注文予約</MenuItem>
                  <MenuItem value="PROMOTION">プロモーション</MenuItem>
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="注文ID（オプション）"
                value={reservationForm.orderId}
                onChange={(e) => setReservationForm({...reservationForm, orderId: e.target.value})}
                className={classes.formControl}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="予約期間（時間）"
                type="number"
                value={reservationForm.reservedHours}
                onChange={(e) => setReservationForm({...reservationForm, reservedHours: e.target.value})}
                className={classes.formControl}
              />
            </Grid>

            <Grid item xs={12}>
              <Button
                variant="contained"
                color="primary"
                onClick={handleReservation}
                disabled={loading || !reservationForm.quantity}
                startIcon={loading ? <CircularProgress size={20} /> : <ReserveIcon />}
              >
                予約作成
              </Button>
            </Grid>

            {/* Active Reservations */}
            <Grid item xs={12}>
              <Typography variant="h6" style={{ marginTop: 16 }}>アクティブな予約</Typography>
              <List>
                {reservations.map((reservation) => (
                  <ListItem key={reservation.id} className={classes.transactionItem}>
                    <ListItemText
                      primary={`数量: ${reservation.reservedQuantity}, タイプ: ${reservation.reservationType}`}
                      secondary={`作成日: ${new Date(reservation.createdAt).toLocaleString()}, 期限: ${reservation.reservedUntil ? new Date(reservation.reservedUntil).toLocaleString() : 'なし'}`}
                    />
                    <Button
                      size="small"
                      color="secondary"
                      onClick={() => handleReleaseReservation(reservation.id)}
                    >
                      解除
                    </Button>
                  </ListItem>
                ))}
                {reservations.length === 0 && (
                  <ListItem>
                    <ListItemText primary="アクティブな予約はありません" />
                  </ListItem>
                )}
              </List>
            </Grid>
          </Grid>
        </TabPanel>

        {/* Transaction History Tab */}
        <TabPanel value={currentTab} index={2} className={classes.tabPanel}>
          <Typography variant="h6" gutterBottom>取引履歴</Typography>
          {loading ? (
            <Box className={classes.loading}>
              <CircularProgress />
            </Box>
          ) : (
            <List>
              {transactions.map((transaction) => (
                <ListItem key={transaction.id} className={classes.transactionItem}>
                  <ListItemText
                    primary={
                      <Box>
                        <Chip
                          label={transaction.type}
                          size="small"
                          color={transaction.type === 'RECEIVE' ? 'primary' : transaction.type === 'SELL' ? 'secondary' : 'default'}
                          className={classes.chip}
                        />
                        <Chip
                          label={transaction.status}
                          size="small"
                          color={transaction.status === 'APPROVED' ? 'primary' : 'default'}
                          className={classes.chip}
                        />
                      </Box>
                    }
                    secondary={
                      <>
                        <Typography variant="body2">
                          数量: {transaction.quantity} ({transaction.beforeQuantity} → {transaction.afterQuantity})
                        </Typography>
                        <Typography variant="body2">
                          実行者: {transaction.executedByUsername}, 実行日: {new Date(transaction.executedAt).toLocaleString()}
                        </Typography>
                        {transaction.reason && (
                          <Typography variant="body2">理由: {transaction.reason}</Typography>
                        )}
                      </>
                    }
                  />
                </ListItem>
              ))}
              {transactions.length === 0 && (
                <ListItem>
                  <ListItemText primary="取引履歴はありません" />
                </ListItem>
              )}
            </List>
          )}
        </TabPanel>

        {/* Barcode Scanner Tab */}
        <TabPanel value={currentTab} index={3} className={classes.tabPanel}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <Typography variant="h6">バーコードスキャン</Typography>
              <Typography variant="body2" color="textSecondary" gutterBottom>
                ISBN-13バーコードによる在庫操作（基盤機能）
              </Typography>
            </Grid>

            <Grid item xs={12} md={8}>
              <TextField
                fullWidth
                label="バーコード（ISBN-13）"
                value={barcodeForm.barcode}
                onChange={(e) => setBarcodeForm({...barcodeForm, barcode: e.target.value})}
                className={classes.formControl}
                placeholder="978-4-XXXXXXXXX"
              />
            </Grid>

            <Grid item xs={12} md={4}>
              <FormControl fullWidth className={classes.formControl}>
                <InputLabel>操作</InputLabel>
                <Select
                  value={barcodeForm.operation}
                  onChange={(e) => setBarcodeForm({...barcodeForm, operation: e.target.value})}
                >
                  <MenuItem value="LOOKUP">在庫照会</MenuItem>
                  <MenuItem value="RECEIVE">入荷処理</MenuItem>
                  <MenuItem value="ADJUST">在庫調整</MenuItem>
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12}>
              <Button
                variant="contained"
                color="primary"
                onClick={handleBarcodeProcess}
                disabled={loading || !barcodeForm.barcode}
                startIcon={loading ? <CircularProgress size={20} /> : <ScannerIcon />}
              >
                バーコード処理
              </Button>
            </Grid>
          </Grid>
        </TabPanel>
      </DialogContent>

      <DialogActions>
        <Button onClick={handleClose}>閉じる</Button>
      </DialogActions>
    </Dialog>
  );
};

export default AdvancedInventoryManagement;