import React from 'react';
import PropTypes from 'prop-types';
import {
  Card,
  CardContent,
  Typography,
  makeStyles,
  Grid,
  Paper,
  Chip,
  Box
} from '@material-ui/core';
import {
  ResponsiveContainer,
  ScatterChart,
  Scatter,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Cell,
  ReferenceLine
} from 'recharts';
import { useI18n } from '../contexts/I18nContext';

const useStyles = makeStyles((theme) => ({
  root: {
    height: '100%',
  },
  matrixGrid: {
    padding: theme.spacing(2),
  },
  quadrantCard: {
    padding: theme.spacing(2),
    height: '100%',
    textAlign: 'center',
  },
  quadrantTitle: {
    marginBottom: theme.spacing(1),
    fontWeight: 'bold',
  },
  itemChip: {
    margin: theme.spacing(0.25),
    fontSize: '0.75rem',
  },
  chartContainer: {
    height: 400,
    marginTop: theme.spacing(2),
  },
  legend: {
    display: 'flex',
    justifyContent: 'center',
    flexWrap: 'wrap',
    marginTop: theme.spacing(2),
  },
  legendItem: {
    display: 'flex',
    alignItems: 'center',
    margin: theme.spacing(0, 1),
  },
  legendColor: {
    width: 12,
    height: 12,
    marginRight: theme.spacing(0.5),
    borderRadius: '50%',
  },
}));

const InventoryRotationMatrix = ({ inventoryData, showChart = true }) => {
  const classes = useStyles();
  const { t } = useI18n();

  if (!inventoryData || inventoryData.length === 0) {
    return (
      <Card className={classes.root}>
        <CardContent>
          <Typography variant="h6">{t('inventory.rotation.matrix', '在庫回転マトリックス')}</Typography>
          <Typography color="textSecondary">{t('ui.no.data', 'データがありません')}</Typography>
        </CardContent>
      </Card>
    );
  }

  // Calculate average turnover rate and days since last sale
  const avgTurnover = inventoryData.reduce((sum, item) => sum + (item.turnoverRate || 0), 0) / inventoryData.length;
  const avgDaysSince = inventoryData.reduce((sum, item) => sum + (item.daysSinceLastSale || 0), 0) / inventoryData.length;

  // Categorize items into quadrants
  const categorizeItem = (item) => {
    const highTurnover = (item.turnoverRate || 0) >= avgTurnover;
    const recentSale = (item.daysSinceLastSale || 0) <= avgDaysSince;

    if (highTurnover && recentSale) return 'STAR'; // High turnover, recent sales
    if (!highTurnover && recentSale) return 'QUESTION'; // Low turnover, recent sales
    if (highTurnover && !recentSale) return 'CASH_COW'; // High turnover, old sales
    return 'DOG'; // Low turnover, old sales
  };

  // Group items by quadrant
  const quadrants = {
    STAR: inventoryData.filter(item => categorizeItem(item) === 'STAR'),
    QUESTION: inventoryData.filter(item => categorizeItem(item) === 'QUESTION'),
    CASH_COW: inventoryData.filter(item => categorizeItem(item) === 'CASH_COW'),
    DOG: inventoryData.filter(item => categorizeItem(item) === 'DOG'),
  };

  // Quadrant configurations
  const quadrantConfig = {
    STAR: {
      title: t('inventory.rotation.quadrant.star', 'スター商品'),
      color: '#4caf50',
      description: t('inventory.rotation.quadrant.star.description', '高回転・新しい売上'),
      backgroundColor: '#e8f5e8',
    },
    QUESTION: {
      title: t('inventory.rotation.quadrant.question', 'クエスチョン商品'),
      color: '#ff9800',
      description: t('inventory.rotation.quadrant.question.description', '低回転・新しい売上'),
      backgroundColor: '#fff3e0',
    },
    CASH_COW: {
      title: t('inventory.rotation.quadrant.cash.cow', 'キャッシュカウ商品'),
      color: '#2196f3',
      description: t('inventory.rotation.quadrant.cash.cow.description', '高回転・古い売上'),
      backgroundColor: '#e3f2fd',
    },
    DOG: {
      title: t('inventory.rotation.quadrant.dog', 'ドッグ商品'),
      color: '#f44336',
      description: t('inventory.rotation.quadrant.dog.description', '低回転・古い売上'),
      backgroundColor: '#ffebee',
    },
  };

  // Custom tooltip for scatter chart
  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload;
      return (
        <Paper style={{ padding: 8 }}>
          <Typography variant="subtitle2">{data.bookTitle}</Typography>
          <Typography variant="body2">{t('inventory.rotation.tooltip.turnover', '回転率')}: {data.turnoverRate?.toFixed(1)}</Typography>
          <Typography variant="body2">{t('inventory.rotation.tooltip.last.sale', '最終販売')}: {data.daysSinceLastSale}{t('inventory.rotation.tooltip.days.ago', '日前')}</Typography>
          <Typography variant="body2">{t('inventory.rotation.tooltip.stock', '在庫')}: {data.currentStock}{t('inventory.rotation.tooltip.books', '冊')}</Typography>
          <Typography variant="body2">{t('inventory.rotation.tooltip.category', '分類')}: {quadrantConfig[categorizeItem(data)]?.title}</Typography>
        </Paper>
      );
    }
    return null;
  };

  return (
    <Card className={classes.root}>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          {t('inventory.rotation.matrix.analysis', '在庫回転マトリックス分析')}
        </Typography>
        
        {showChart && (
          <div className={classes.chartContainer}>
            <ResponsiveContainer width="100%" height="100%">
              <ScatterChart data={inventoryData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis 
                  dataKey="daysSinceLastSale" 
                  name="最終販売からの日数"
                  label={{ value: '最終販売からの日数', position: 'insideBottom', offset: -5 }}
                />
                <YAxis 
                  dataKey="turnoverRate" 
                  name="回転率"
                  label={{ value: '回転率', angle: -90, position: 'insideLeft' }}
                />
                <Tooltip content={<CustomTooltip />} />
                
                {/* Reference lines for quadrant divisions */}
                <ReferenceLine 
                  x={avgDaysSince} 
                  stroke="#666" 
                  strokeDasharray="5 5"
                  label="平均日数"
                />
                <ReferenceLine 
                  y={avgTurnover} 
                  stroke="#666" 
                  strokeDasharray="5 5"
                  label="平均回転率"
                />
                
                <Scatter dataKey="turnoverRate">
                  {inventoryData.map((entry, index) => (
                    <Cell 
                      key={`cell-${index}`} 
                      fill={quadrantConfig[categorizeItem(entry)]?.color}
                    />
                  ))}
                </Scatter>
              </ScatterChart>
            </ResponsiveContainer>
          </div>
        )}

        {/* Quadrant Matrix */}
        <Grid container spacing={2} className={classes.matrixGrid}>
          <Grid item xs={12} sm={6}>
            <Paper 
              className={classes.quadrantCard}
              style={{ backgroundColor: quadrantConfig.STAR.backgroundColor }}
            >
              <Typography variant="h6" className={classes.quadrantTitle} style={{ color: quadrantConfig.STAR.color }}>
                {quadrantConfig.STAR.title}
              </Typography>
              <Typography variant="body2" color="textSecondary" gutterBottom>
                {quadrantConfig.STAR.description}
              </Typography>
              <Typography variant="h4" style={{ color: quadrantConfig.STAR.color }}>
                {quadrants.STAR.length}
              </Typography>
              <Box mt={1}>
                {quadrants.STAR.slice(0, 5).map((item, index) => (
                  <Chip
                    key={index}
                    label={item.bookTitle?.length > 15 ? 
                      `${item.bookTitle.substring(0, 15)}...` : 
                      item.bookTitle}
                    size="small"
                    className={classes.itemChip}
                    style={{ backgroundColor: quadrantConfig.STAR.color, color: 'white' }}
                  />
                ))}
                {quadrants.STAR.length > 5 && (
                  <Typography variant="caption" color="textSecondary">
                    他 {quadrants.STAR.length - 5} 商品
                  </Typography>
                )}
              </Box>
            </Paper>
          </Grid>

          <Grid item xs={12} sm={6}>
            <Paper 
              className={classes.quadrantCard}
              style={{ backgroundColor: quadrantConfig.QUESTION.backgroundColor }}
            >
              <Typography variant="h6" className={classes.quadrantTitle} style={{ color: quadrantConfig.QUESTION.color }}>
                {quadrantConfig.QUESTION.title}
              </Typography>
              <Typography variant="body2" color="textSecondary" gutterBottom>
                {quadrantConfig.QUESTION.description}
              </Typography>
              <Typography variant="h4" style={{ color: quadrantConfig.QUESTION.color }}>
                {quadrants.QUESTION.length}
              </Typography>
              <Box mt={1}>
                {quadrants.QUESTION.slice(0, 5).map((item, index) => (
                  <Chip
                    key={index}
                    label={item.bookTitle?.length > 15 ? 
                      `${item.bookTitle.substring(0, 15)}...` : 
                      item.bookTitle}
                    size="small"
                    className={classes.itemChip}
                    style={{ backgroundColor: quadrantConfig.QUESTION.color, color: 'white' }}
                  />
                ))}
                {quadrants.QUESTION.length > 5 && (
                  <Typography variant="caption" color="textSecondary">
                    他 {quadrants.QUESTION.length - 5} 商品
                  </Typography>
                )}
              </Box>
            </Paper>
          </Grid>

          <Grid item xs={12} sm={6}>
            <Paper 
              className={classes.quadrantCard}
              style={{ backgroundColor: quadrantConfig.CASH_COW.backgroundColor }}
            >
              <Typography variant="h6" className={classes.quadrantTitle} style={{ color: quadrantConfig.CASH_COW.color }}>
                {quadrantConfig.CASH_COW.title}
              </Typography>
              <Typography variant="body2" color="textSecondary" gutterBottom>
                {quadrantConfig.CASH_COW.description}
              </Typography>
              <Typography variant="h4" style={{ color: quadrantConfig.CASH_COW.color }}>
                {quadrants.CASH_COW.length}
              </Typography>
              <Box mt={1}>
                {quadrants.CASH_COW.slice(0, 5).map((item, index) => (
                  <Chip
                    key={index}
                    label={item.bookTitle?.length > 15 ? 
                      `${item.bookTitle.substring(0, 15)}...` : 
                      item.bookTitle}
                    size="small"
                    className={classes.itemChip}
                    style={{ backgroundColor: quadrantConfig.CASH_COW.color, color: 'white' }}
                  />
                ))}
                {quadrants.CASH_COW.length > 5 && (
                  <Typography variant="caption" color="textSecondary">
                    他 {quadrants.CASH_COW.length - 5} 商品
                  </Typography>
                )}
              </Box>
            </Paper>
          </Grid>

          <Grid item xs={12} sm={6}>
            <Paper 
              className={classes.quadrantCard}
              style={{ backgroundColor: quadrantConfig.DOG.backgroundColor }}
            >
              <Typography variant="h6" className={classes.quadrantTitle} style={{ color: quadrantConfig.DOG.color }}>
                {quadrantConfig.DOG.title}
              </Typography>
              <Typography variant="body2" color="textSecondary" gutterBottom>
                {quadrantConfig.DOG.description}
              </Typography>
              <Typography variant="h4" style={{ color: quadrantConfig.DOG.color }}>
                {quadrants.DOG.length}
              </Typography>
              <Box mt={1}>
                {quadrants.DOG.slice(0, 5).map((item, index) => (
                  <Chip
                    key={index}
                    label={item.bookTitle?.length > 15 ? 
                      `${item.bookTitle.substring(0, 15)}...` : 
                      item.bookTitle}
                    size="small"
                    className={classes.itemChip}
                    style={{ backgroundColor: quadrantConfig.DOG.color, color: 'white' }}
                  />
                ))}
                {quadrants.DOG.length > 5 && (
                  <Typography variant="caption" color="textSecondary">
                    他 {quadrants.DOG.length - 5} 商品
                  </Typography>
                )}
              </Box>
            </Paper>
          </Grid>
        </Grid>

        {/* Legend */}
        <div className={classes.legend}>
          {Object.entries(quadrantConfig).map(([key, config]) => (
            <div key={key} className={classes.legendItem}>
              <div className={classes.legendColor} style={{ backgroundColor: config.color }} />
              <Typography variant="body2">{config.title}</Typography>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
};

InventoryRotationMatrix.propTypes = {
  inventoryData: PropTypes.arrayOf(PropTypes.shape({
    bookTitle: PropTypes.string.isRequired,
    turnoverRate: PropTypes.number,
    daysSinceLastSale: PropTypes.number,
    currentStock: PropTypes.number,
  })),
  showChart: PropTypes.bool,
};

export default InventoryRotationMatrix;