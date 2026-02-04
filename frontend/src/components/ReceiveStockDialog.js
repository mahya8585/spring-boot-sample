import React, { useState, useCallback } from 'react';
import { useDispatch } from 'react-redux';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Grid,
  Typography,
  Box,
  IconButton,
} from '@material-ui/core';
import { Close as CloseIcon } from '@material-ui/icons';
import { makeStyles } from '@material-ui/core/styles';
import { receiveStock } from '../store/actions/inventoryActions';
import { useI18n } from '../contexts/I18nContext';

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
}));

const ReceiveStockDialog = ({ open, onClose, inventory, onSuccess }) => {
  const classes = useStyles();
  const dispatch = useDispatch();
  const { t } = useI18n();

  const [formData, setFormData] = useState({
    quantity: '',
    location: 'STORE',
    reason: '',
    deliveryNote: '',
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
      newErrors.quantity = '入荷数量は必須です';
    } else {
      const quantity = Number(formData.quantity);
      if (isNaN(quantity) || quantity <= 0) {
        newErrors.quantity = '入荷数量は正の数値を入力してください';
      } else if (!Number.isInteger(quantity)) {
        newErrors.quantity = '入荷数量は整数を入力してください';
      } else if (quantity > 10000) {
        newErrors.quantity = '入荷数量は10,000以下で入力してください';
      }
    }

    // Location validation
    if (!formData.location || !['STORE', 'WAREHOUSE'].includes(formData.location)) {
      newErrors.location = '入荷場所を選択してください';
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
      await dispatch(receiveStock({
        bookId: inventory.bookId,
        quantity: parseInt(formData.quantity),
        location: formData.location,
        reason: formData.reason?.trim() || undefined,
        deliveryNote: formData.deliveryNote?.trim() || undefined,
      }));
      
      onSuccess();
      handleClose();
    } catch (error) {
      console.error('Receive stock error:', error);
      setErrors({
        general: error.response?.data?.message || error.message || '入荷処理中にエラーが発生しました',
      });
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setFormData({
      quantity: '',
      location: 'STORE',
      reason: '',
      deliveryNote: '',
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
        <span>{t('receive.dialog.title', '入荷登録')}</span>
        <IconButton onClick={handleClose} size="small">
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent>
        <Box className={classes.bookInfo}>
          <Typography variant="subtitle1" gutterBottom>
            <strong>{t('inventory.book.title', '書籍タイトル')}</strong>
          </Typography>
          <Typography variant="body2">
            <strong>タイトル:</strong> {inventory.bookTitle}
          </Typography>
          <Typography variant="body2">
            <strong>ISBN:</strong> {inventory.isbn13}
          </Typography>
          <Typography variant="body2">
            <strong>現在の店頭在庫:</strong> {inventory.storeStock}
          </Typography>
          <Typography variant="body2">
            <strong>現在の倉庫在庫:</strong> {inventory.warehouseStock}
          </Typography>
        </Box>

        <form onSubmit={(e) => { e.preventDefault(); handleSubmit(); }}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label={`${t('receive.quantity', '入荷数量')} *`}
                type="number"
                value={formData.quantity}
                onChange={handleInputChange('quantity')}
                error={!!errors.quantity}
                helperText={errors.quantity}
                className={classes.formControl}
                inputProps={{ min: 1, 'aria-label': t('receive.quantity', '入荷数量') }}
                id="receive-quantity-input"
                autoFocus
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth className={classes.formControl}>
                <InputLabel id="location-select-label">{t('receive.location', '入荷場所')} *</InputLabel>
                <Select
                  labelId="location-select-label"
                  value={formData.location}
                  onChange={handleInputChange('location')}
                  aria-label={t('receive.location', '入荷場所')}
                  error={!!errors.location}
                >
                  <MenuItem value="STORE">{t('inventory.location.store', '店頭')}</MenuItem>
                  <MenuItem value="WAREHOUSE">{t('inventory.location.warehouse', '倉庫')}</MenuItem>
                </Select>
                {errors.location && (
                  <Typography variant="caption" color="error" className={classes.error}>
                    {errors.location}
                  </Typography>
                )}
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label={t('receive.reason', '入荷理由・備考')}
                multiline
                rows={2}
                value={formData.reason}
                onChange={handleInputChange('reason')}
                className={classes.formControl}
                id="receive-reason-input"
                aria-label={t('receive.reason', '入荷理由・備考')}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label={t('receive.delivery.note', '納品書番号')}
                value={formData.deliveryNote}
                onChange={handleInputChange('deliveryNote')}
                className={classes.formControl}
                id="delivery-note-input"
                aria-label={t('receive.delivery.note', '納品書番号')}
              />
            </Grid>
          </Grid>
        </form>

        {errors.general && (
          <Typography variant="body2" className={classes.error}>
            {errors.general}
          </Typography>
        )}
      </DialogContent>

      <DialogActions>
        <Button onClick={handleClose} disabled={loading}>
          {t('form.cancel', 'キャンセル')}
        </Button>
        <Button
          onClick={handleSubmit}
          color="primary"
          variant="contained"
          disabled={loading}
        >
          {loading ? '処理中...' : t('inventory.receive', '入荷実行')}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ReceiveStockDialog;