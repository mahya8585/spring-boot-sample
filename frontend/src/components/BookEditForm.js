import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  Card,
  CardContent,
  Typography,
  Grid,
  TextField,
  Button,
  MenuItem,
  FormControl,
  InputLabel,
  Select,
  Box,
} from '@material-ui/core';
import { Alert } from '@material-ui/lab';
import { makeStyles } from '@material-ui/core/styles';
import { 
  Save as SaveIcon,
  Cancel as CancelIcon,
} from '@material-ui/icons';
import { 
  updateBook,
  setEditMode
} from '../store/actions/booksActions';
import { useI18n } from '../contexts/I18nContext';

const useStyles = makeStyles((theme) => ({
  formCard: {
    marginBottom: theme.spacing(2),
    transition: 'all 0.3s ease-in-out',
    animation: '$slideIn 0.5s ease-out',
  },
  '@keyframes slideIn': {
    '0%': {
      opacity: 0,
      transform: 'translateY(-20px)',
    },
    '100%': {
      opacity: 1,
      transform: 'translateY(0)',
    },
  },
  formTitle: {
    marginBottom: theme.spacing(2),
    display: 'flex',
    alignItems: 'center',
  },
  formField: {
    marginBottom: theme.spacing(2),
  },
  actions: {
    display: 'flex',
    justifyContent: 'flex-end',
    gap: theme.spacing(2),
    marginTop: theme.spacing(2),
    [theme.breakpoints.down('sm')]: {
      flexDirection: 'column-reverse',
      gap: theme.spacing(1),
    },
  },
  readOnlyField: {
    backgroundColor: theme.palette.grey[100],
  },
}));

const TECH_LEVELS = [
  { value: 'BEGINNER', label: '初級' },
  { value: 'INTERMEDIATE', label: '中級' },
  { value: 'ADVANCED', label: '上級' },
];

const BookEditForm = ({ book, onCancel }) => {
  const classes = useStyles();
  const dispatch = useDispatch();
  const { t } = useI18n();
  
  const { loading } = useSelector(state => state.books);
  
  const [formData, setFormData] = useState({
    id: '',
    isbn13: '',
    title: '',
    titleEn: '',
    publisherName: '',
    publicationDate: '',
    edition: '',
    listPrice: '',
    sellingPrice: '',
    pages: '',
    level: '',
    versionInfo: '',
    sampleCodeUrl: '',
  });
  
  const [formErrors, setFormErrors] = useState({});
  const [submitError, setSubmitError] = useState(null);

  useEffect(() => {
    if (book) {
      setFormData({
        id: book.id || '',
        isbn13: book.isbn13 || '',
        title: book.title || '',
        titleEn: book.titleEn || '',
        publisherName: book.publisherName || '',
        publicationDate: book.publicationDate || '',
        edition: book.edition || '',
        listPrice: book.listPrice || '',
        sellingPrice: book.sellingPrice || '',
        pages: book.pages || '',
        level: book.level || '',
        versionInfo: book.versionInfo || '',
        sampleCodeUrl: book.sampleCodeUrl || '',
      });
    }
  }, [book]);

  const handleFieldChange = (field) => (event) => {
    const value = event.target.value;
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
    
    // Clear field error when user starts typing
    if (formErrors[field]) {
      setFormErrors(prev => ({
        ...prev,
        [field]: null
      }));
    }
  };

  const validateForm = () => {
    const errors = {};

    // Title is required
    if (!formData.title.trim()) {
      errors.title = 'タイトルは必須入力です';
    }

    // Price validation (positive numbers with up to 2 decimal places)
    if (formData.listPrice && (isNaN(formData.listPrice) || parseFloat(formData.listPrice) < 0)) {
      errors.listPrice = '定価は正の数値で入力してください';
    }
    
    if (formData.sellingPrice && (isNaN(formData.sellingPrice) || parseFloat(formData.sellingPrice) < 0)) {
      errors.sellingPrice = '販売価格は正の数値で入力してください';
    }

    // Pages validation (positive integer)
    if (formData.pages && (isNaN(formData.pages) || parseInt(formData.pages) < 0 || !Number.isInteger(parseFloat(formData.pages)))) {
      errors.pages = 'ページ数は正の整数で入力してください';
    }

    // Edition validation (positive integer)
    if (formData.edition && (isNaN(formData.edition) || parseInt(formData.edition) < 0 || !Number.isInteger(parseFloat(formData.edition)))) {
      errors.edition = '版次は正の整数で入力してください';
    }

    // URL validation
    if (formData.sampleCodeUrl && formData.sampleCodeUrl.trim()) {
      try {
        new URL(formData.sampleCodeUrl);
      } catch {
        errors.sampleCodeUrl = '有効なURLを入力してください';
      }
    }

    // Publication date validation (past date only)
    if (formData.publicationDate) {
      const inputDate = new Date(formData.publicationDate);
      const today = new Date();
      if (inputDate > today) {
        errors.publicationDate = '発行日は過去の日付のみ入力可能です';
      }
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSubmitError(null);

    if (!validateForm()) {
      return;
    }

    try {
      // Prepare data for API
      const updateData = {
        id: formData.id,
        isbn13: formData.isbn13, // Read-only but still included
        title: formData.title.trim(),
        titleEn: formData.titleEn.trim() || null,
        // Note: publisherName is read-only in this form
        publicationDate: formData.publicationDate || null,
        edition: formData.edition ? parseInt(formData.edition) : null,
        listPrice: formData.listPrice ? parseFloat(formData.listPrice) : null,
        sellingPrice: formData.sellingPrice ? parseFloat(formData.sellingPrice) : null,
        pages: formData.pages ? parseInt(formData.pages) : null,
        level: formData.level || null,
        versionInfo: formData.versionInfo.trim() || null,
        sampleCodeUrl: formData.sampleCodeUrl.trim() || null,
      };

      await dispatch(updateBook(formData.id, updateData));
      dispatch(setEditMode(false));
    } catch (error) {
      setSubmitError(error.message || '更新中にエラーが発生しました');
    }
  };

  const handleCancel = () => {
    setFormErrors({});
    setSubmitError(null);
    dispatch(setEditMode(false));
    if (onCancel) {
      onCancel();
    }
  };

  return (
    <Card className={classes.formCard}>
      <CardContent>
        <Typography variant="h6" className={classes.formTitle}>
          書籍情報編集
        </Typography>

        {submitError && (
          <Alert severity="error" style={{ marginBottom: 16 }}>
            {submitError}
          </Alert>
        )}

        <form onSubmit={handleSubmit}>
          <Grid container spacing={3}>
            {/* Read-only fields */}
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="ISBN-13"
                value={formData.isbn13}
                disabled
                className={classes.readOnlyField}
                helperText="ISBN-13は変更できません"
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="出版社"
                value={formData.publisherName}
                disabled
                className={classes.readOnlyField}
                helperText="出版社は変更できません"
              />
            </Grid>

            {/* Editable fields */}
            <Grid item xs={12}>
              <TextField
                fullWidth
                required
                label={t('book.title', 'タイトル')}
                value={formData.title}
                onChange={handleFieldChange('title')}
                error={!!formErrors.title}
                helperText={formErrors.title}
                className={classes.formField}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                fullWidth
                label={t('book.title.english', '英語タイトル')}
                value={formData.titleEn}
                onChange={handleFieldChange('titleEn')}
                className={classes.formField}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                type="date"
                label={t('book.publication.date', '発行日')}
                value={formData.publicationDate}
                onChange={handleFieldChange('publicationDate')}
                error={!!formErrors.publicationDate}
                helperText={formErrors.publicationDate}
                InputLabelProps={{ shrink: true }}
                className={classes.formField}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                type="number"
                label={t('book.edition', '版次')}
                value={formData.edition}
                onChange={handleFieldChange('edition')}
                error={!!formErrors.edition}
                helperText={formErrors.edition}
                className={classes.formField}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                type="number"
                label={t('book.list.price', '定価')}
                value={formData.listPrice}
                onChange={handleFieldChange('listPrice')}
                error={!!formErrors.listPrice}
                helperText={formErrors.listPrice}
                inputProps={{ step: "0.01", min: "0" }}
                className={classes.formField}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                type="number"
                label={t('book.selling.price', '販売価格')}
                value={formData.sellingPrice}
                onChange={handleFieldChange('sellingPrice')}
                error={!!formErrors.sellingPrice}
                helperText={formErrors.sellingPrice}
                inputProps={{ step: "0.01", min: "0" }}
                className={classes.formField}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                type="number"
                label="ページ数"
                value={formData.pages}
                onChange={handleFieldChange('pages')}
                error={!!formErrors.pages}
                helperText={formErrors.pages}
                inputProps={{ min: "0" }}
                className={classes.formField}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <FormControl fullWidth className={classes.formField}>
                <InputLabel>技術レベル</InputLabel>
                <Select
                  value={formData.level}
                  onChange={handleFieldChange('level')}
                >
                  <MenuItem value="">
                    <em>選択してください</em>
                  </MenuItem>
                  {TECH_LEVELS.map((level) => (
                    <MenuItem key={level.value} value={level.value}>
                      {level.label}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12}>
              <TextField
                fullWidth
                label={t('book.version.info', 'バージョン情報')}
                value={formData.versionInfo}
                onChange={handleFieldChange('versionInfo')}
                className={classes.formField}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                fullWidth
                label={t('book.sample.code.url', 'サンプルコードURL')}
                value={formData.sampleCodeUrl}
                onChange={handleFieldChange('sampleCodeUrl')}
                error={!!formErrors.sampleCodeUrl}
                helperText={formErrors.sampleCodeUrl}
                className={classes.formField}
              />
            </Grid>
          </Grid>

          <Box className={classes.actions}>
            <Button
              onClick={handleCancel}
              disabled={loading}
              startIcon={<CancelIcon />}
            >
              {t('form.cancel', 'キャンセル')}
            </Button>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              disabled={loading}
              startIcon={<SaveIcon />}
            >
              {loading ? '保存中...' : t('form.save', '保存')}
            </Button>
          </Box>
        </form>
      </CardContent>
    </Card>
  );
};

export default BookEditForm;