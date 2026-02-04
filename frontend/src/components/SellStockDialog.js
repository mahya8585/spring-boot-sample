import React, { useState, useCallback } from 'react';
import { useDispatch } from 'react-redux';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Grid,
  Typography,
  Box,
  IconButton,
} from '@material-ui/core';
import { Close as CloseIcon } from '@material-ui/icons';
import { makeStyles } from '@material-ui/core/styles';
import { sellStock } from '../store/actions/inventoryActions';

const useStyles = makeStyles((theme) => ({
  dialog: {
    '& .MuiDialog-paper': {
      minWidth: '500px',
      maxWidth: '600px',
    },
  },
  title: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingRight: theme.spacing(1),
  },
  formControl: {
    marginBottom: theme.spacing(2),
  },
  bookInfo: {
    backgroundColor: theme.palette.grey[100],
    padding: theme.spacing(2),
    borderRadius: theme.shape.borderRadius,
    marginBottom: theme.spacing(2),
  },
  error: {
    color: theme.palette.error.main,
    marginTop: theme.spacing(1),
  },
  warning: {
    color: theme.palette.warning.main,
    marginTop: theme.spacing(1),
  },
}));

const SellStockDialog = ({ open, onClose, inventory, onSuccess }) => {
  const classes = useStyles();
  const dispatch = useDispatch();

  const [formData, setFormData] = useState({
    quantity: '',
    customerId: '',
    reason: '',
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleInputChange = useCallback((field) => (event) => {
    setFormData({
      ...formData,
      [field]: event.target.value,
    });
    // Clear error when user starts typing
    if (errors[field]) {
      setErrors({
        ...errors,
        [field]: '',
      });
    }
  }, [formData, errors]);

  const validateForm = () => {
    const newErrors = {};

    // Quantity validation
    if (!formData.quantity || formData.quantity.toString().trim() === '') {
      newErrors.quantity = '販売数量は必須です';
    } else {
      const quantity = Number(formData.quantity);
      if (isNaN(quantity) || quantity <= 0) {
        newErrors.quantity = '販売数量は正の数値を入力してください';
      } else if (!Number.isInteger(quantity)) {
        newErrors.quantity = '販売数量は整数を入力してください';
      } else if (quantity > inventory.storeStock) {
        newErrors.quantity = `店頭在庫が不足しています（在庫: ${inventory.storeStock}）`;
      }
    }

    // Customer ID validation (optional but if provided should be valid)
    if (formData.customerId && formData.customerId.trim().length > 0) {
      const customerIdPattern = /^[A-Za-z0-9_-]+$/;
      if (!customerIdPattern.test(formData.customerId.trim())) {
        newErrors.customerId = '顧客IDは英数字、ハイフン、アンダースコアのみ使用できます';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validateForm()) {
      return;
    }

    setLoading(true);
    setErrors({});
    
    try {
      await dispatch(sellStock({
        bookId: inventory.bookId,
        quantity: parseInt(formData.quantity),
        customerId: formData.customerId?.trim() || undefined,
        reason: formData.reason?.trim() || undefined,
      }));
      
      onSuccess();
      handleClose();
    } catch (error) {
      console.error('Sell stock error:', error);
      setErrors({
        general: error.response?.data?.message || error.message || '販売処理中にエラーが発生しました',
      });
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setFormData({
      quantity: '',
      customerId: '',
      reason: '',
    });
    setErrors({});
    setLoading(false);
    onClose();
  };

  if (!inventory) {
    return null;
  }

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      className={classes.dialog}
      maxWidth="sm"
      fullWidth
    >
      <DialogTitle className={classes.title}>
        <span>販売処理</span>
        <IconButton onClick={handleClose} size="small">
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent>
        <Box className={classes.bookInfo}>
          <Typography variant="subtitle1" gutterBottom>
            <strong>書籍情報</strong>
          </Typography>
          <Typography variant="body2">
            <strong>タイトル:</strong> {inventory.bookTitle}
          </Typography>
          <Typography variant="body2">
            <strong>ISBN:</strong> {inventory.isbn13}
          </Typography>
          <Typography variant="body2">
            <strong>店頭在庫:</strong> {inventory.storeStock}
          </Typography>
          <Typography variant="body2">
            <strong>倉庫在庫:</strong> {inventory.warehouseStock}
          </Typography>
        </Box>

        {inventory.storeStock <= 0 && (
          <Typography variant="body2" className={classes.error}>
            ⚠️ 店頭在庫がありません。販売できません。
          </Typography>
        )}

        <form onSubmit={(e) => { e.preventDefault(); handleSubmit(); }}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="販売数量 *"
                type="number"
                value={formData.quantity}
                onChange={handleInputChange('quantity')}
                error={!!errors.quantity}
                helperText={errors.quantity}
                className={classes.formControl}
                inputProps={{ min: 1, max: inventory.storeStock, 'aria-label': '販売数量' }}
                disabled={inventory.storeStock <= 0}
                id="sell-quantity-input"
                autoFocus={inventory.storeStock > 0}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="顧客ID"
                value={formData.customerId}
                onChange={handleInputChange('customerId')}
                className={classes.formControl}
                placeholder="例: CUST001"
                id="customer-id-input"
                aria-label="顧客ID"
                error={!!errors.customerId}
                helperText={errors.customerId}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="販売理由・備考"
                multiline
                rows={2}
                value={formData.reason}
                onChange={handleInputChange('reason')}
                className={classes.formControl}
                id="sell-reason-input"
                aria-label="販売理由・備考"
              />
            </Grid>
          </Grid>
        </form>

        {parseInt(formData.quantity) > 0 && parseInt(formData.quantity) <= inventory.storeStock && (
          <Typography variant="body2" className={classes.warning}>
            販売後の店頭在庫: {inventory.storeStock - parseInt(formData.quantity)}
          </Typography>
        )}

        {errors.general && (
          <Typography variant="body2" className={classes.error}>
            {errors.general}
          </Typography>
        )}
      </DialogContent>

      <DialogActions>
        <Button onClick={handleClose} disabled={loading}>
          キャンセル
        </Button>
        <Button
          onClick={handleSubmit}
          color="secondary"
          variant="contained"
          disabled={loading || inventory.storeStock <= 0}
        >
          {loading ? '処理中...' : '販売実行'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default SellStockDialog;