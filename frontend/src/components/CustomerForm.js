import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams, useHistory } from 'react-router-dom';
import {
  Card,
  CardContent,
  Typography,
  Grid,
  TextField,
  Button,
  Box,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  CircularProgress,
  Divider,
} from '@material-ui/core';
import { Alert } from '@material-ui/lab';
import { makeStyles } from '@material-ui/core/styles';
import { 
  Save as SaveIcon,
  Cancel as CancelIcon,
  ArrowBack as BackIcon,
} from '@material-ui/icons';
import { 
  createCustomer,
  updateCustomer,
  fetchCustomerById,
  clearCustomerError
} from '../store/actions/customersActions';

// Email validation helper
const isValidEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

// Postal code validation helper (Japanese format)
const isValidPostalCode = (postalCode) => {
  if (!postalCode) return true; // Optional field
  const postalCodeRegex = /^(\d{3}-\d{4}|\d{7})$/;
  return postalCodeRegex.test(postalCode);
};

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
  form: {
    marginTop: theme.spacing(2),
  },
  formSection: {
    marginBottom: theme.spacing(3),
  },
  buttonGroup: {
    marginTop: theme.spacing(3),
    display: 'flex',
    gap: theme.spacing(2),
  },
  loadingContainer: {
    display: 'flex',
    justifyContent: 'center',
    padding: theme.spacing(4),
  },
}));

const CustomerForm = () => {
  const classes = useStyles();
  const dispatch = useDispatch();
  const history = useHistory();
  const { id } = useParams();
  const isEdit = Boolean(id);
  
  const {
    selectedCustomer,
    createLoading,
    updateLoading,
    detailLoading,
    createError,
    updateError,
    detailError,
  } = useSelector(state => state.customers);

  const [formData, setFormData] = useState({
    customerType: 'INDIVIDUAL',
    name: '',
    nameKana: '',
    email: '',
    phone: '',
    birthDate: '',
    gender: '',
    occupation: '',
    companyName: '',
    department: '',
    postalCode: '',
    address: '',
    notes: '',
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (isEdit && id) {
      dispatch(fetchCustomerById(id));
    }
    
    return () => {
      dispatch(clearCustomerError());
    };
  }, [dispatch, id, isEdit]);

  useEffect(() => {
    if (isEdit && selectedCustomer) {
      setFormData({
        customerType: selectedCustomer.customerType || 'INDIVIDUAL',
        name: selectedCustomer.name || '',
        nameKana: selectedCustomer.nameKana || '',
        email: selectedCustomer.email || '',
        phone: selectedCustomer.phone || '',
        birthDate: selectedCustomer.birthDate || '',
        gender: selectedCustomer.gender || '',
        occupation: selectedCustomer.occupation || '',
        companyName: selectedCustomer.companyName || '',
        department: selectedCustomer.department || '',
        postalCode: selectedCustomer.postalCode || '',
        address: selectedCustomer.address || '',
        notes: selectedCustomer.notes || '',
      });
    }
  }, [isEdit, selectedCustomer]);

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear field error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.customerType) {
      newErrors.customerType = '顧客タイプは必須です';
    }
    
    if (!formData.name.trim()) {
      newErrors.name = '名前は必須です';
    } else if (formData.name.length > 100) {
      newErrors.name = '名前は100文字以内で入力してください';
    }
    
    if (!formData.email.trim()) {
      newErrors.email = 'メールアドレスは必須です';
    } else if (!isValidEmail(formData.email)) {
      newErrors.email = '正しいメールアドレスを入力してください';
    } else if (formData.email.length > 255) {
      newErrors.email = 'メールアドレスは255文字以内で入力してください';
    }
    
    if (!formData.phone.trim()) {
      newErrors.phone = '電話番号は必須です';
    } else if (formData.phone.length > 20) {
      newErrors.phone = '電話番号は20文字以内で入力してください';
    }
    
    // Postal code validation
    if (formData.postalCode && !isValidPostalCode(formData.postalCode)) {
      newErrors.postalCode = '郵便番号は「123-4567」または「1234567」の形式で入力してください';
    }
    
    // Optional field length validations
    if (formData.nameKana && formData.nameKana.length > 100) {
      newErrors.nameKana = 'ふりがなは100文字以内で入力してください';
    }
    
    if (formData.occupation && formData.occupation.length > 100) {
      newErrors.occupation = '職業は100文字以内で入力してください';
    }
    
    if (formData.companyName && formData.companyName.length > 100) {
      newErrors.companyName = '会社名は100文字以内で入力してください';
    }
    
    if (formData.department && formData.department.length > 100) {
      newErrors.department = '部署は100文字以内で入力してください';
    }
    
    // Birth date validation
    if (formData.birthDate) {
      const birthDate = new Date(formData.birthDate);
      const today = new Date();
      if (birthDate >= today) {
        newErrors.birthDate = '生年月日は過去の日付を入力してください';
      }
    }
    
    // Corporate specific validation
    if (formData.customerType === 'CORPORATE' && !formData.companyName.trim()) {
      newErrors.companyName = '法人の場合、会社名は必須です';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    try {
      const customerData = {
        ...formData,
        // Remove empty optional fields
        nameKana: formData.nameKana || null,
        birthDate: formData.birthDate || null,
        gender: formData.gender || null,
        occupation: formData.occupation || null,
        companyName: formData.companyName || null,
        department: formData.department || null,
        postalCode: formData.postalCode || null,
        address: formData.address || null,
        notes: formData.notes || null,
      };
      
      if (isEdit) {
        await dispatch(updateCustomer(id, customerData));
        history.push(`/customers/${id}`);
      } else {
        const newCustomer = await dispatch(createCustomer(customerData));
        history.push(`/customers/${newCustomer.id}`);
      }
    } catch (error) {
      console.error('Failed to save customer:', error);
    }
  };

  const handleCancel = () => {
    if (isEdit) {
      history.push(`/customers/${id}`);
    } else {
      history.push('/customers');
    }
  };

  const handleBack = () => {
    history.push('/customers');
  };

  if (isEdit && detailLoading) {
    return (
      <div className={classes.loadingContainer}>
        <CircularProgress />
      </div>
    );
  }

  const loading = createLoading || updateLoading;
  const error = createError || updateError || detailError;

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
            {isEdit ? '顧客編集' : '新規顧客登録'}
          </Typography>
        </Box>
      </div>

      {error && (
        <Alert severity="error" style={{ marginBottom: 16 }}>
          {error}
        </Alert>
      )}

      <Card>
        <CardContent>
          <form onSubmit={handleSubmit} className={classes.form}>
            
            {/* 基本情報 */}
            <div className={classes.formSection}>
              <Typography variant="h6" gutterBottom>
                基本情報
              </Typography>
              
              <Grid container spacing={3}>
                <Grid item xs={12} sm={6}>
                  <FormControl fullWidth variant="outlined" error={Boolean(errors.customerType)}>
                    <InputLabel>顧客タイプ *</InputLabel>
                    <Select
                      name="customerType"
                      value={formData.customerType}
                      onChange={handleInputChange}
                      label="顧客タイプ *"
                    >
                      <MenuItem value="INDIVIDUAL">個人</MenuItem>
                      <MenuItem value="CORPORATE">法人</MenuItem>
                    </Select>
                  </FormControl>
                  {errors.customerType && (
                    <Typography variant="caption" color="error">
                      {errors.customerType}
                    </Typography>
                  )}
                </Grid>

                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label={formData.customerType === 'CORPORATE' ? '会社名 *' : '名前 *'}
                    name="name"
                    value={formData.name}
                    onChange={handleInputChange}
                    variant="outlined"
                    error={Boolean(errors.name)}
                    helperText={errors.name}
                  />
                </Grid>

                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="フリガナ"
                    name="nameKana"
                    value={formData.nameKana}
                    onChange={handleInputChange}
                    variant="outlined"
                    placeholder="ヤマダタロウ"
                  />
                </Grid>

                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="メールアドレス *"
                    name="email"
                    type="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    variant="outlined"
                    error={Boolean(errors.email)}
                    helperText={errors.email}
                  />
                </Grid>

                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="電話番号 *"
                    name="phone"
                    value={formData.phone}
                    onChange={handleInputChange}
                    variant="outlined"
                    error={Boolean(errors.phone)}
                    helperText={errors.phone}
                    placeholder="090-1234-5678"
                  />
                </Grid>

                {formData.customerType === 'INDIVIDUAL' && (
                  <>
                    <Grid item xs={12} sm={6}>
                      <TextField
                        fullWidth
                        label="生年月日"
                        name="birthDate"
                        type="date"
                        value={formData.birthDate}
                        onChange={handleInputChange}
                        variant="outlined"
                        InputLabelProps={{
                          shrink: true,
                        }}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                      <FormControl fullWidth variant="outlined">
                        <InputLabel>性別</InputLabel>
                        <Select
                          name="gender"
                          value={formData.gender}
                          onChange={handleInputChange}
                          label="性別"
                        >
                          <MenuItem value="">選択しない</MenuItem>
                          <MenuItem value="MALE">男性</MenuItem>
                          <MenuItem value="FEMALE">女性</MenuItem>
                          <MenuItem value="OTHER">その他</MenuItem>
                        </Select>
                      </FormControl>
                    </Grid>
                  </>
                )}
              </Grid>
            </div>

            <Divider />

            {/* 職業・会社情報 */}
            <div className={classes.formSection}>
              <Typography variant="h6" gutterBottom>
                職業・会社情報
              </Typography>
              
              <Grid container spacing={3}>
                {formData.customerType === 'INDIVIDUAL' && (
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="職業"
                      name="occupation"
                      value={formData.occupation}
                      onChange={handleInputChange}
                      variant="outlined"
                      placeholder="ソフトウェアエンジニア"
                    />
                  </Grid>
                )}

                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label={formData.customerType === 'CORPORATE' ? '会社名 *' : '会社名'}
                    name="companyName"
                    value={formData.companyName}
                    onChange={handleInputChange}
                    variant="outlined"
                    error={Boolean(errors.companyName)}
                    helperText={errors.companyName}
                  />
                </Grid>

                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="部署名"
                    name="department"
                    value={formData.department}
                    onChange={handleInputChange}
                    variant="outlined"
                    placeholder="開発部"
                  />
                </Grid>
              </Grid>
            </div>

            <Divider />

            {/* 住所情報 */}
            <div className={classes.formSection}>
              <Typography variant="h6" gutterBottom>
                住所情報
              </Typography>
              
              <Grid container spacing={3}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="郵便番号"
                    name="postalCode"
                    value={formData.postalCode}
                    onChange={handleInputChange}
                    variant="outlined"
                    placeholder="123-4567"
                  />
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="住所"
                    name="address"
                    value={formData.address}
                    onChange={handleInputChange}
                    variant="outlined"
                    placeholder="東京都渋谷区渋谷1-1-1"
                  />
                </Grid>
              </Grid>
            </div>

            <Divider />

            {/* 備考 */}
            <div className={classes.formSection}>
              <Typography variant="h6" gutterBottom>
                備考
              </Typography>
              
              <TextField
                fullWidth
                label="備考"
                name="notes"
                value={formData.notes}
                onChange={handleInputChange}
                variant="outlined"
                multiline
                rows={4}
                placeholder="その他の情報があれば記入してください"
              />
            </div>

            {/* ボタン */}
            <div className={classes.buttonGroup}>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                startIcon={<SaveIcon />}
                disabled={loading}
              >
                {loading ? <CircularProgress size={20} /> : (isEdit ? '更新' : '登録')}
              </Button>
              
              <Button
                variant="outlined"
                startIcon={<CancelIcon />}
                onClick={handleCancel}
                disabled={loading}
              >
                キャンセル
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};

export default CustomerForm;