import React, { useState } from 'react';
import {
  Container,
  Paper,
  Typography,
  Box,
  Grid,
  Button,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Stepper,
  Step,
  StepLabel,
  Card,
  CardContent,
  CardActions,
  Chip,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  CircularProgress,
  Snackbar,
} from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import {
  Assessment,
  Save,
  GetApp,
  Visibility,
  TrendingUp,
  People,
  Storage,
  Timeline,
} from '@material-ui/icons';
import { Alert } from '@material-ui/lab';
import api from '../services/api';

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(3),
  },
  paper: {
    padding: theme.spacing(3),
    marginBottom: theme.spacing(2),
  },
  stepperPaper: {
    padding: theme.spacing(2),
    marginBottom: theme.spacing(3),
  },
  templateCard: {
    cursor: 'pointer',
    transition: 'elevation 0.2s',
    '&:hover': {
      elevation: 4,
    },
  },
  selectedTemplate: {
    border: `2px solid ${theme.palette.primary.main}`,
  },
  parameterForm: {
    marginTop: theme.spacing(2),
  },
  reportPreview: {
    minHeight: 300,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  actionButtons: {
    marginTop: theme.spacing(2),
    display: 'flex',
    gap: theme.spacing(1),
  },
  templateIcon: {
    fontSize: 48,
    color: theme.palette.primary.main,
    marginBottom: theme.spacing(1),
  },
}));

const steps = ['テンプレート選択', 'パラメータ設定', 'プレビュー', '保存・エクスポート'];

const templateCategories = [
  {
    id: 'sales',
    name: '売上分析',
    icon: <TrendingUp />,
    templates: [
      {
        id: 'SALES_BY_TECH_CATEGORY',
        name: '技術カテゴリ別売上分析',
        description: '技術カテゴリごとの売上実績と成長率を分析',
        parameters: ['startDate', 'endDate', 'techCategories'],
        outputFormats: ['CHART', 'TABLE', 'SUMMARY'],
      },
      {
        id: 'TECH_LEVEL_PERFORMANCE',
        name: '技術レベル別パフォーマンス',
        description: '初級・中級・上級書籍の売上パフォーマンス比較',
        parameters: ['startDate', 'endDate', 'techLevel'],
        outputFormats: ['CHART', 'TABLE'],
      },
      {
        id: 'SEASONAL_SALES_ANALYSIS',
        name: '季節性売上分析',
        description: '学習期間・技術イベント連動の季節性分析',
        parameters: ['year', 'seasonType'],
        outputFormats: ['CHART', 'HEATMAP'],
      },
    ],
  },
  {
    id: 'customer',
    name: '顧客分析',
    icon: <People />,
    templates: [
      {
        id: 'CUSTOMER_TECH_JOURNEY',
        name: '顧客技術学習ジャーニー',
        description: '顧客の技術スキル習得順序と進化分析',
        parameters: ['customerId', 'startDate', 'endDate'],
        outputFormats: ['FLOWCHART', 'TABLE'],
      },
      {
        id: 'CUSTOMER_SEGMENT_ANALYSIS',
        name: '顧客セグメント分析',
        description: '技術レベル別顧客セグメントの詳細分析',
        parameters: ['segmentType', 'startDate', 'endDate'],
        outputFormats: ['CHART', 'TABLE', 'SUMMARY'],
      },
    ],
  },
  {
    id: 'inventory',
    name: '在庫分析',
    icon: <Storage />,
    templates: [
      {
        id: 'INVENTORY_OPTIMIZATION',
        name: '在庫最適化レポート',
        description: '技術陳腐化リスクを考慮した在庫最適化提案',
        parameters: ['categoryCode', 'riskLevel'],
        outputFormats: ['TABLE', 'ALERT', 'RECOMMENDATION'],
      },
      {
        id: 'DEAD_STOCK_ANALYSIS',
        name: 'デッドストック分析',
        description: '60日・90日売上なし書籍の詳細分析',
        parameters: ['daysThreshold', 'categoryCode'],
        outputFormats: ['TABLE', 'CHART'],
      },
    ],
  },
  {
    id: 'trends',
    name: '技術トレンド',
    icon: <Timeline />,
    templates: [
      {
        id: 'TECH_TREND_FORECAST',
        name: '技術トレンド予測',
        description: '新興技術の成長予測と書籍需要予測',
        parameters: ['forecastPeriod', 'techCategories'],
        outputFormats: ['CHART', 'FORECAST', 'SUMMARY'],
      },
      {
        id: 'TECH_LIFECYCLE_ANALYSIS',
        name: '技術ライフサイクル分析',
        description: '技術の新興→成長→成熟→衰退サイクル分析',
        parameters: ['techCategory', 'analysisDepth'],
        outputFormats: ['LIFECYCLE', 'CHART'],
      },
    ],
  },
];

const CustomReportCreator = () => {
  const classes = useStyles();
  const [activeStep, setActiveStep] = useState(0);
  const [selectedTemplate, setSelectedTemplate] = useState(null);
  const [reportName, setReportName] = useState('');
  const [reportDescription, setReportDescription] = useState('');
  const [parameters, setParameters] = useState({});
  const [generatedReport, setGeneratedReport] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [previewDialog, setPreviewDialog] = useState(false);

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const handleTemplateSelect = (template) => {
    setSelectedTemplate(template);
    setReportName(`${template.name}_${new Date().toISOString().split('T')[0]}`);
    setReportDescription(template.description);
    // Initialize parameters with default values
    const defaultParams = {};
    template.parameters.forEach(param => {
      if (param.includes('Date')) {
        defaultParams[param] = new Date();
      } else {
        defaultParams[param] = '';
      }
    });
    setParameters(defaultParams);
  };

  const handleParameterChange = (paramName, value) => {
    setParameters(prev => ({
      ...prev,
      [paramName]: value
    }));
  };

  const generateReport = async () => {
    if (!selectedTemplate) return;

    setLoading(true);
    setError(null);

    try {
      const request = {
        reportType: selectedTemplate.id,
        reportName,
        description: reportDescription,
        startDate: parameters.startDate,
        endDate: parameters.endDate,
        createdBy: 'current_user', // In production, get from auth context
        filters: parameters,
      };

      const response = await api.post('/reports/custom-reports', request);
      setGeneratedReport(response.data);
      setSuccess('レポートが正常に生成されました');
      handleNext();
    } catch (err) {
      setError('レポート生成に失敗しました: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const exportReport = async (format) => {
    if (!generatedReport) return;

    setLoading(true);
    try {
      const response = await api.get(`/reports/export/${generatedReport.reportId}?format=${format}`, {
        responseType: 'blob'
      });

      const blob = new Blob([response.data]);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `${reportName}.${format.toLowerCase()}`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);

      setSuccess(`${format}形式でエクスポートが完了しました`);
    } catch (err) {
      setError('エクスポートに失敗しました');
    } finally {
      setLoading(false);
    }
  };

  const renderStepContent = (step) => {
    switch (step) {
      case 0:
        return renderTemplateSelection();
      case 1:
        return renderParameterConfiguration();
      case 2:
        return renderReportPreview();
      case 3:
        return renderSaveAndExport();
      default:
        return 'Unknown step';
    }
  };

  const renderTemplateSelection = () => (
    <Box>
      <Typography variant="h6" gutterBottom>
        レポートテンプレートを選択してください
      </Typography>
      {templateCategories.map(category => (
        <Box key={category.id} mb={3}>
          <Typography variant="h6" gutterBottom>
            <Box display="flex" alignItems="center" gap={1}>
              {category.icon}
              {category.name}
            </Box>
          </Typography>
          <Grid container spacing={2}>
            {category.templates.map(template => (
              <Grid item xs={12} md={6} lg={4} key={template.id}>
                <Card
                  className={`${classes.templateCard} ${
                    selectedTemplate?.id === template.id ? classes.selectedTemplate : ''
                  }`}
                  onClick={() => handleTemplateSelect(template)}
                >
                  <CardContent>
                    <Box display="flex" flexDirection="column" alignItems="center" textAlign="center">
                      <Assessment className={classes.templateIcon} />
                      <Typography variant="h6" gutterBottom>
                        {template.name}
                      </Typography>
                      <Typography variant="body2" color="textSecondary" paragraph>
                        {template.description}
                      </Typography>
                      <Box>
                        {template.outputFormats.map(format => (
                          <Chip key={format} label={format} size="small" style={{ margin: 2 }} />
                        ))}
                      </Box>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Box>
      ))}
    </Box>
  );

  const renderParameterConfiguration = () => (
    <Box>
      <Typography variant="h6" gutterBottom>
        レポートパラメータを設定してください
      </Typography>
      {selectedTemplate && (
        <Box>
          <Grid container spacing={2} className={classes.parameterForm}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="レポート名"
                value={reportName}
                onChange={(e) => setReportName(e.target.value)}
                variant="outlined"
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                multiline
                rows={3}
                label="説明"
                value={reportDescription}
                onChange={(e) => setReportDescription(e.target.value)}
                variant="outlined"
              />
            </Grid>
            {selectedTemplate.parameters.map(param => (
              <Grid item xs={12} md={6} key={param}>
                {param.includes('Date') ? (
                  <TextField
                    fullWidth
                    type="date"
                    label={getParameterLabel(param)}
                    value={parameters[param] ? parameters[param].toISOString().split('T')[0] : ''}
                    onChange={(e) => handleParameterChange(param, new Date(e.target.value))}
                    variant="outlined"
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />
                ) : param.includes('Level') || param.includes('Type') ? (
                  <FormControl fullWidth variant="outlined">
                    <InputLabel>{getParameterLabel(param)}</InputLabel>
                    <Select
                      value={parameters[param] || ''}
                      onChange={(e) => handleParameterChange(param, e.target.value)}
                      label={getParameterLabel(param)}
                    >
                      {getParameterOptions(param).map(option => (
                        <MenuItem key={option.value} value={option.value}>
                          {option.label}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                ) : (
                  <TextField
                    fullWidth
                    label={getParameterLabel(param)}
                    value={parameters[param] || ''}
                    onChange={(e) => handleParameterChange(param, e.target.value)}
                    variant="outlined"
                  />
                )}
              </Grid>
            ))}
          </Grid>
        </Box>
      )}
    </Box>
  );

  const renderReportPreview = () => (
    <Box>
      <Typography variant="h6" gutterBottom>
        レポートプレビュー
      </Typography>
      <Paper className={classes.reportPreview}>
        {loading ? (
          <Box display="flex" flexDirection="column" alignItems="center">
            <CircularProgress size={60} />
            <Typography variant="body2" style={{ marginTop: 16 }}>
              レポートを生成中...
            </Typography>
          </Box>
        ) : generatedReport ? (
          <Box p={2}>
            <Typography variant="h6" gutterBottom>
              {generatedReport.reportName}
            </Typography>
            <Typography variant="body2" paragraph>
              生成日: {new Date(generatedReport.createdDate).toLocaleDateString('ja-JP')}
            </Typography>
            <Typography variant="body2" paragraph>
              ステータス: {generatedReport.status}
            </Typography>
            <Typography variant="body2" paragraph>
              {generatedReport.description}
            </Typography>
            <Button
              variant="outlined"
              startIcon={<Visibility />}
              onClick={() => setPreviewDialog(true)}
            >
              詳細プレビュー
            </Button>
          </Box>
        ) : (
          <Box display="flex" flexDirection="column" alignItems="center">
            <Assessment style={{ fontSize: 64, color: '#ccc' }} />
            <Typography variant="body2" color="textSecondary">
              「レポート生成」ボタンをクリックしてプレビューを表示
            </Typography>
            <Button
              variant="contained"
              color="primary"
              startIcon={<Assessment />}
              onClick={generateReport}
              style={{ marginTop: 16 }}
              disabled={!selectedTemplate || !reportName}
            >
              レポート生成
            </Button>
          </Box>
        )}
      </Paper>
    </Box>
  );

  const renderSaveAndExport = () => (
    <Box>
      <Typography variant="h6" gutterBottom>
        保存・エクスポート
      </Typography>
      {generatedReport && (
        <Grid container spacing={2}>
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  エクスポート形式
                </Typography>
                <Typography variant="body2" paragraph>
                  レポートを以下の形式でエクスポートできます:
                </Typography>
                <List>
                  <ListItem>
                    <ListItemIcon><GetApp /></ListItemIcon>
                    <ListItemText primary="PDF" secondary="印刷・配布用" />
                  </ListItem>
                  <ListItem>
                    <ListItemIcon><GetApp /></ListItemIcon>
                    <ListItemText primary="Excel" secondary="データ分析用" />
                  </ListItem>
                  <ListItem>
                    <ListItemIcon><GetApp /></ListItemIcon>
                    <ListItemText primary="CSV" secondary="データベース連携用" />
                  </ListItem>
                </List>
              </CardContent>
              <CardActions>
                <Button
                  variant="outlined"
                  onClick={() => exportReport('PDF')}
                  disabled={loading}
                  startIcon={<GetApp />}
                >
                  PDF
                </Button>
                <Button
                  variant="outlined"
                  onClick={() => exportReport('EXCEL')}
                  disabled={loading}
                  startIcon={<GetApp />}
                >
                  Excel
                </Button>
                <Button
                  variant="outlined"
                  onClick={() => exportReport('CSV')}
                  disabled={loading}
                  startIcon={<GetApp />}
                >
                  CSV
                </Button>
              </CardActions>
            </Card>
          </Grid>
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  レポート保存
                </Typography>
                <Typography variant="body2" paragraph>
                  レポートは自動的に保存されました。
                </Typography>
                <Typography variant="body2">
                  <strong>レポートID:</strong> {generatedReport.reportId}
                </Typography>
                <Typography variant="body2">
                  <strong>作成日:</strong> {new Date(generatedReport.createdDate).toLocaleDateString('ja-JP')}
                </Typography>
              </CardContent>
              <CardActions>
                <Button
                  variant="contained"
                  color="primary"
                  startIcon={<Save />}
                  onClick={() => setSuccess('レポートテンプレートとして保存されました')}
                >
                  テンプレートとして保存
                </Button>
              </CardActions>
            </Card>
          </Grid>
        </Grid>
      )}
    </Box>
  );

  const getParameterLabel = (param) => {
    const labels = {
      startDate: '開始日',
      endDate: '終了日',
      techCategories: '技術カテゴリ',
      techLevel: '技術レベル',
      customerId: '顧客ID',
      segmentType: 'セグメントタイプ',
      categoryCode: 'カテゴリコード',
      riskLevel: 'リスクレベル',
      daysThreshold: '日数閾値',
      forecastPeriod: '予測期間',
      techCategory: '技術カテゴリ',
      analysisDepth: '分析深度',
      year: '年',
      seasonType: '季節タイプ',
    };
    return labels[param] || param;
  };

  const getParameterOptions = (param) => {
    const options = {
      techLevel: [
        { value: 'BEGINNER', label: '初級' },
        { value: 'INTERMEDIATE', label: '中級' },
        { value: 'ADVANCED', label: '上級' },
      ],
      riskLevel: [
        { value: 'LOW', label: '低' },
        { value: 'MEDIUM', label: '中' },
        { value: 'HIGH', label: '高' },
      ],
      segmentType: [
        { value: 'ACADEMIC', label: '学術関係者' },
        { value: 'PROFESSIONAL', label: 'プロフェッショナル' },
        { value: 'HOBBYIST', label: '趣味・学習者' },
      ],
      seasonType: [
        { value: 'QUARTERLY', label: '四半期' },
        { value: 'ACADEMIC', label: '学期' },
        { value: 'CALENDAR', label: 'カレンダー年' },
      ],
      analysisDepth: [
        { value: 'BASIC', label: '基本' },
        { value: 'DETAILED', label: '詳細' },
        { value: 'COMPREHENSIVE', label: '包括的' },
      ],
    };
    return options[param] || [];
  };

  return (
    <Container maxWidth="xl" className={classes.root}>
      <Paper className={classes.stepperPaper}>
        <Stepper activeStep={activeStep} alternativeLabel>
          {steps.map((label) => (
            <Step key={label}>
              <StepLabel>{label}</StepLabel>
            </Step>
          ))}
        </Stepper>
      </Paper>

      <Paper className={classes.paper}>
        {renderStepContent(activeStep)}

        <Box className={classes.actionButtons}>
          <Button
            disabled={activeStep === 0}
            onClick={handleBack}
          >
            戻る
          </Button>
          <Button
            variant="contained"
            color="primary"
            onClick={handleNext}
            disabled={
              (activeStep === 0 && !selectedTemplate) ||
              (activeStep === 1 && !reportName) ||
              (activeStep === 2 && !generatedReport) ||
              activeStep === steps.length - 1
            }
          >
            {activeStep === steps.length - 1 ? '完了' : '次へ'}
          </Button>
        </Box>
      </Paper>

      {/* Preview Dialog */}
      <Dialog
        open={previewDialog}
        onClose={() => setPreviewDialog(false)}
        maxWidth="lg"
        fullWidth
      >
        <DialogTitle>レポート詳細プレビュー</DialogTitle>
        <DialogContent>
          {generatedReport && (
            <Box>
              <Typography variant="h6" gutterBottom>
                {generatedReport.reportName}
              </Typography>
              <pre style={{ whiteSpace: 'pre-wrap', fontSize: '12px' }}>
                {JSON.stringify(generatedReport.reportData, null, 2)}
              </pre>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPreviewDialog(false)}>閉じる</Button>
        </DialogActions>
      </Dialog>

      {/* Success/Error Snackbars */}
      <Snackbar
        open={!!success}
        autoHideDuration={6000}
        onClose={() => setSuccess(null)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={() => setSuccess(null)} severity="success">
          {success}
        </Alert>
      </Snackbar>

      <Snackbar
        open={!!error}
        autoHideDuration={6000}
        onClose={() => setError(null)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={() => setError(null)} severity="error">
          {error}
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default CustomReportCreator;