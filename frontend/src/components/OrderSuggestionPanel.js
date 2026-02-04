import React, { useState } from 'react';
import PropTypes from 'prop-types';
import {
  Card,
  CardContent,
  Typography,
  makeStyles,
  Chip,
  Box,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  LinearProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  IconButton,
  Tooltip
} from '@material-ui/core';
import { Alert } from '@material-ui/lab';
import {
  ShoppingCart,
  Warning,
  CheckCircle,
  Schedule,
  TrendingUp,
  ExpandMore,
  Info,
  Add,
  Remove
} from '@material-ui/icons';

const useStyles = makeStyles((theme) => ({
  root: {
    height: '100%',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: theme.spacing(2),
  },
  priorityChip: {
    fontWeight: 'bold',
  },
  suggestionCard: {
    margin: theme.spacing(1, 0),
    border: `1px solid ${theme.palette.divider}`,
  },
  urgencyChip: {
    margin: theme.spacing(0, 0.5),
  },
  metricsBox: {
    display: 'flex',
    justifyContent: 'space-around',
    marginBottom: theme.spacing(2),
    padding: theme.spacing(2),
    backgroundColor: theme.palette.grey[100],
    borderRadius: theme.shape.borderRadius,
  },
  metricItem: {
    textAlign: 'center',
  },
  metricValue: {
    fontSize: '1.5rem',
    fontWeight: 'bold',
    color: theme.palette.primary.main,
  },
  metricLabel: {
    fontSize: '0.75rem',
    color: theme.palette.text.secondary,
  },
  riskBox: {
    padding: theme.spacing(2),
    margin: theme.spacing(2, 0),
    borderRadius: theme.shape.borderRadius,
    backgroundColor: theme.palette.error.light,
    color: theme.palette.error.contrastText,
  },
  optimizationBox: {
    padding: theme.spacing(2),
    margin: theme.spacing(2, 0),
    borderRadius: theme.shape.borderRadius,
    backgroundColor: theme.palette.success.light,
    color: theme.palette.success.contrastText,
  },
  quantityControls: {
    display: 'flex',
    alignItems: 'center',
    gap: theme.spacing(1),
  },
  actionButtons: {
    display: 'flex',
    gap: theme.spacing(1),
    marginTop: theme.spacing(2),
  },
}));

const OrderSuggestionPanel = ({ 
  orderSuggestions,
  onOrderApprove,
  onOrderReject,
  onQuantityChange 
}) => {
  const classes = useStyles();
  const [selectedSuggestions, setSelectedSuggestions] = useState(new Set());
  const [optimizationDialogOpen, setOptimizationDialogOpen] = useState(false);

  if (!orderSuggestions) {
    return (
      <Card className={classes.root}>
        <CardContent>
          <Typography variant="h6">インテリジェント発注提案</Typography>
          <Typography color="textSecondary">データがありません</Typography>
        </CardContent>
      </Card>
    );
  }

  // Get urgency color
  const getUrgencyColor = (urgency) => {
    switch (urgency) {
      case 'IMMEDIATE': return 'secondary';
      case 'WITHIN_WEEK': return 'primary';
      case 'WITHIN_MONTH': return 'default';
      default: return 'default';
    }
  };

  // Get urgency icon
  const getUrgencyIcon = (urgency) => {
    switch (urgency) {
      case 'IMMEDIATE': return <Warning />;
      case 'WITHIN_WEEK': return <Schedule />;
      case 'WITHIN_MONTH': return <CheckCircle />;
      default: return <Info />;
    }
  };

  // Format currency
  const formatCurrency = (value) => {
    return new Intl.NumberFormat('ja-JP', {
      style: 'currency',
      currency: 'JPY',
      notation: 'compact'
    }).format(value || 0);
  };

  // Handle suggestion selection
  const handleSuggestionToggle = (suggestionId) => {
    const newSelected = new Set(selectedSuggestions);
    if (newSelected.has(suggestionId)) {
      newSelected.delete(suggestionId);
    } else {
      newSelected.add(suggestionId);
    }
    setSelectedSuggestions(newSelected);
  };

  // Handle quantity change
  const handleQuantityChange = (bookId, newQuantity) => {
    if (onQuantityChange) {
      onQuantityChange(bookId, newQuantity);
    }
  };

  // Handle bulk order approval
  const handleBulkApprove = () => {
    if (onOrderApprove) {
      selectedSuggestions.forEach(id => {
        const suggestion = orderSuggestions.bookSuggestions.find(s => s.bookId === id);
        if (suggestion) {
          onOrderApprove(suggestion);
        }
      });
    }
    setSelectedSuggestions(new Set());
  };

  return (
    <Card className={classes.root}>
      <CardContent>
        <div className={classes.header}>
          <Typography variant="h6">
            <ShoppingCart style={{ verticalAlign: 'middle', marginRight: 8 }} />
            インテリジェント発注提案
          </Typography>
          <Box>
            <Chip
              label={orderSuggestions.priority}
              color={orderSuggestions.priority === 'HIGH' ? 'secondary' : 'primary'}
              size="small"
              className={classes.priorityChip}
            />
            <Chip
              label={orderSuggestions.suggestionType}
              variant="outlined"
              size="small"
              style={{ marginLeft: 8 }}
            />
          </Box>
        </div>

        {/* Key Metrics */}
        <div className={classes.metricsBox}>
          <div className={classes.metricItem}>
            <Typography className={classes.metricValue}>
              {orderSuggestions.totalSuggestions}
            </Typography>
            <Typography className={classes.metricLabel}>
              提案数
            </Typography>
          </div>
          <div className={classes.metricItem}>
            <Typography className={classes.metricValue}>
              {formatCurrency(orderSuggestions.totalOrderValue)}
            </Typography>
            <Typography className={classes.metricLabel}>
              総発注額
            </Typography>
          </div>
          {orderSuggestions.optimization && (
            <>
              <div className={classes.metricItem}>
                <Typography className={classes.metricValue}>
                  {formatCurrency(orderSuggestions.optimization.expectedRevenue)}
                </Typography>
                <Typography className={classes.metricLabel}>
                  予想売上
                </Typography>
              </div>
              <div className={classes.metricItem}>
                <Typography className={classes.metricValue}>
                  {((orderSuggestions.optimization.expectedProfit / orderSuggestions.optimization.suggestedSpending) * 100).toFixed(1)}%
                </Typography>
                <Typography className={classes.metricLabel}>
                  予想ROI
                </Typography>
              </div>
            </>
          )}
        </div>

        {/* Optimization Alert */}
        {orderSuggestions.optimization && (
          <Alert 
            severity="info" 
            action={
              <Button 
                color="inherit" 
                size="small"
                onClick={() => setOptimizationDialogOpen(true)}
              >
                詳細
              </Button>
            }
            style={{ marginBottom: 16 }}
          >
            予算最適化: {formatCurrency(orderSuggestions.optimization.suggestedSpending)} / {formatCurrency(orderSuggestions.optimization.totalBudget)}
            <LinearProgress 
              variant="determinate" 
              value={(orderSuggestions.optimization.suggestedSpending / orderSuggestions.optimization.totalBudget) * 100}
              style={{ marginTop: 8 }}
            />
          </Alert>
        )}

        {/* Book Suggestions Table */}
        {orderSuggestions.bookSuggestions && orderSuggestions.bookSuggestions.length > 0 && (
          <Accordion defaultExpanded>
            <AccordionSummary expandIcon={<ExpandMore />}>
              <Typography variant="subtitle1">
                書籍別発注提案 ({orderSuggestions.bookSuggestions.length}件)
              </Typography>
            </AccordionSummary>
            <AccordionDetails>
              <TableContainer component={Paper} style={{ width: '100%' }}>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell padding="checkbox"></TableCell>
                      <TableCell>書籍名</TableCell>
                      <TableCell align="right">現在在庫</TableCell>
                      <TableCell align="right">提案数量</TableCell>
                      <TableCell align="right">単価</TableCell>
                      <TableCell align="right">合計</TableCell>
                      <TableCell>理由</TableCell>
                      <TableCell>緊急度</TableCell>
                      <TableCell>予想ROI</TableCell>
                      <TableCell>アクション</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {orderSuggestions.bookSuggestions.map((suggestion, index) => (
                      <TableRow key={suggestion.bookId || index}>
                        <TableCell padding="checkbox">
                          <input
                            type="checkbox"
                            checked={selectedSuggestions.has(suggestion.bookId)}
                            onChange={() => handleSuggestionToggle(suggestion.bookId)}
                          />
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2" fontWeight="bold">
                            {suggestion.bookTitle}
                          </Typography>
                          <Typography variant="caption" color="textSecondary">
                            {suggestion.isbn13}
                          </Typography>
                        </TableCell>
                        <TableCell align="right">{suggestion.currentStock}</TableCell>
                        <TableCell align="right">
                          <div className={classes.quantityControls}>
                            <IconButton 
                              size="small" 
                              onClick={() => handleQuantityChange(suggestion.bookId, suggestion.suggestedQuantity - 1)}
                            >
                              <Remove />
                            </IconButton>
                            <Typography>{suggestion.suggestedQuantity}</Typography>
                            <IconButton 
                              size="small" 
                              onClick={() => handleQuantityChange(suggestion.bookId, suggestion.suggestedQuantity + 1)}
                            >
                              <Add />
                            </IconButton>
                          </div>
                        </TableCell>
                        <TableCell align="right">{formatCurrency(suggestion.unitCost)}</TableCell>
                        <TableCell align="right">{formatCurrency(suggestion.totalCost)}</TableCell>
                        <TableCell>
                          <Chip 
                            label={suggestion.reason} 
                            size="small"
                            color={suggestion.reason === 'LOW_STOCK' ? 'secondary' : 'default'}
                          />
                        </TableCell>
                        <TableCell>
                          <Tooltip title={suggestion.daysUntilStockout ? `${suggestion.daysUntilStockout}日で在庫切れ` : ''}>
                            <Chip
                              icon={getUrgencyIcon(suggestion.urgency)}
                              label={suggestion.urgency}
                              size="small"
                              color={getUrgencyColor(suggestion.urgency)}
                              className={classes.urgencyChip}
                            />
                          </Tooltip>
                        </TableCell>
                        <TableCell align="right">
                          {suggestion.expectedRoi && (
                            <Typography color="primary">
                              {suggestion.expectedRoi.toFixed(1)}%
                            </Typography>
                          )}
                        </TableCell>
                        <TableCell>
                          <Button
                            size="small"
                            color="primary"
                            onClick={() => onOrderApprove && onOrderApprove(suggestion)}
                          >
                            承認
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </AccordionDetails>
          </Accordion>
        )}

        {/* Category Suggestions */}
        {orderSuggestions.categorySuggestions && orderSuggestions.categorySuggestions.length > 0 && (
          <Accordion>
            <AccordionSummary expandIcon={<ExpandMore />}>
              <Typography variant="subtitle1">
                カテゴリ別戦略提案
              </Typography>
            </AccordionSummary>
            <AccordionDetails>
              <Box width="100%">
                {orderSuggestions.categorySuggestions.map((category, index) => (
                  <Paper key={index} style={{ padding: 16, marginBottom: 16 }}>
                    <Typography variant="subtitle2" gutterBottom>
                      {category.categoryName}
                    </Typography>
                    <Typography variant="body2" color="textSecondary">
                      アイテム数: {category.itemCount} | 
                      総発注額: {formatCurrency(category.totalOrderValue)} | 
                      トレンド: <Chip label={category.trend} size="small" color="primary" />
                    </Typography>
                    {category.strategicRecommendation && (
                      <Typography variant="body2" style={{ marginTop: 8 }}>
                        戦略提案: {category.strategicRecommendation}
                      </Typography>
                    )}
                    {category.topBooks && category.topBooks.length > 0 && (
                      <Typography variant="body2" color="textSecondary" style={{ marginTop: 4 }}>
                        注目書籍: {category.topBooks.join(', ')}
                      </Typography>
                    )}
                  </Paper>
                ))}
              </Box>
            </AccordionDetails>
          </Accordion>
        )}

        {/* Risk Factors */}
        {orderSuggestions.riskFactors && orderSuggestions.riskFactors.length > 0 && (
          <Accordion>
            <AccordionSummary expandIcon={<ExpandMore />}>
              <Typography variant="subtitle1">
                <Warning style={{ verticalAlign: 'middle', marginRight: 8 }} />
                リスク要因
              </Typography>
            </AccordionSummary>
            <AccordionDetails>
              <List style={{ width: '100%' }}>
                {orderSuggestions.riskFactors.map((risk, index) => (
                  <ListItem key={index}>
                    <ListItemIcon>
                      <Warning color={risk.severity === 'HIGH' ? 'error' : 'warning'} />
                    </ListItemIcon>
                    <ListItemText
                      primary={risk.riskType}
                      secondary={
                        <>
                          <Typography variant="body2">{risk.description}</Typography>
                          <Typography variant="body2" color="textSecondary">
                            対策: {risk.mitigation}
                          </Typography>
                        </>
                      }
                    />
                    <Chip 
                      label={risk.severity} 
                      size="small"
                      color={risk.severity === 'HIGH' ? 'secondary' : 'default'}
                    />
                  </ListItem>
                ))}
              </List>
            </AccordionDetails>
          </Accordion>
        )}

        {/* Action Buttons */}
        <div className={classes.actionButtons}>
          <Button
            variant="contained"
            color="primary"
            disabled={selectedSuggestions.size === 0}
            onClick={handleBulkApprove}
            startIcon={<CheckCircle />}
          >
            選択項目を一括承認 ({selectedSuggestions.size})
          </Button>
          <Button
            variant="outlined"
            onClick={() => setSelectedSuggestions(new Set())}
            disabled={selectedSuggestions.size === 0}
          >
            選択をクリア
          </Button>
        </div>

        {/* Optimization Dialog */}
        <Dialog open={optimizationDialogOpen} onClose={() => setOptimizationDialogOpen(false)} maxWidth="md">
          <DialogTitle>発注最適化詳細</DialogTitle>
          <DialogContent>
            {orderSuggestions.optimization && (
              <Box>
                <Typography variant="h6" gutterBottom>財務影響</Typography>
                <Typography>総予算: {formatCurrency(orderSuggestions.optimization.totalBudget)}</Typography>
                <Typography>推奨支出: {formatCurrency(orderSuggestions.optimization.suggestedSpending)}</Typography>
                <Typography>予想売上: {formatCurrency(orderSuggestions.optimization.expectedRevenue)}</Typography>
                <Typography>予想利益: {formatCurrency(orderSuggestions.optimization.expectedProfit)}</Typography>
                <Typography>キャッシュフロー影響: {orderSuggestions.optimization.cashFlowImpact}</Typography>
                <Typography>リスクレベル: {orderSuggestions.optimization.riskLevel}</Typography>
                
                {orderSuggestions.optimization.optimizationRecommendations && (
                  <Box mt={2}>
                    <Typography variant="h6" gutterBottom>最適化推奨事項</Typography>
                    <List>
                      {orderSuggestions.optimization.optimizationRecommendations.map((rec, index) => (
                        <ListItem key={index}>
                          <ListItemIcon><TrendingUp /></ListItemIcon>
                          <ListItemText primary={rec} />
                        </ListItem>
                      ))}
                    </List>
                  </Box>
                )}
              </Box>
            )}
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOptimizationDialogOpen(false)}>閉じる</Button>
          </DialogActions>
        </Dialog>
      </CardContent>
    </Card>
  );
};

OrderSuggestionPanel.propTypes = {
  orderSuggestions: PropTypes.shape({
    suggestionType: PropTypes.string,
    priority: PropTypes.string,
    totalSuggestions: PropTypes.number,
    totalOrderValue: PropTypes.number,
    bookSuggestions: PropTypes.arrayOf(PropTypes.object),
    categorySuggestions: PropTypes.arrayOf(PropTypes.object),
    optimization: PropTypes.object,
    riskFactors: PropTypes.arrayOf(PropTypes.object),
  }),
  onOrderApprove: PropTypes.func,
  onOrderReject: PropTypes.func,
  onQuantityChange: PropTypes.func,
};

export default OrderSuggestionPanel;