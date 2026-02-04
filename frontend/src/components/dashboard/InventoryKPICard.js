import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  CircularProgress,
  Chip
} from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import { Storage, Warning } from '@material-ui/icons';

const useStyles = makeStyles((theme) => ({
  card: {
    height: '100%',
    position: 'relative',
    background: 'linear-gradient(135deg, #ff9a56 0%, #ff6b6b 100%)',
    color: 'white',
  },
  cardContent: {
    padding: theme.spacing(2),
  },
  icon: {
    fontSize: 40,
    marginBottom: theme.spacing(1),
    opacity: 0.9,
  },
  mainValue: {
    fontSize: '2rem',
    fontWeight: 'bold',
    marginBottom: theme.spacing(1),
  },
  subValue: {
    fontSize: '0.875rem',
    opacity: 0.8,
    marginBottom: theme.spacing(0.5),
  },
  alertChip: {
    backgroundColor: 'rgba(255,255,255,0.2)',
    color: 'white',
    fontWeight: 'bold'
  },
  loading: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: 120,
  },
}));

const InventoryKPICard = ({ data, loading }) => {
  const classes = useStyles();

  if (loading) {
    return (
      <Card className={classes.card}>
        <CardContent className={classes.loading}>
          <CircularProgress size={40} style={{ color: 'white' }} />
        </CardContent>
      </Card>
    );
  }

  const hasAlerts = (data?.lowStockItems || 0) > 0 || (data?.outOfStockItems || 0) > 0;

  return (
    <Card className={classes.card}>
      <CardContent className={classes.cardContent}>
        <Box display="flex" alignItems="center" justifyContent="space-between">
          <Storage className={classes.icon} />
          {hasAlerts && (
            <Chip
              icon={<Warning />}
              label="要注意"
              size="small"
              className={classes.alertChip}
            />
          )}
        </Box>
        
        <Typography variant="h6" gutterBottom>
          在庫指標
        </Typography>
        
        <Typography className={classes.mainValue}>
          {data?.totalProducts || 0}
        </Typography>
        
        <Typography className={classes.subValue}>
          総商品数
        </Typography>
        
        <Typography className={classes.subValue}>
          低在庫: {data?.lowStockItems || 0}件
        </Typography>
        
        <Typography className={classes.subValue}>
          在庫切れ: {data?.outOfStockItems || 0}件
        </Typography>
        
        <Typography className={classes.subValue}>
          回転率: {data?.inventoryTurnover || 0}回/年
        </Typography>
      </CardContent>
    </Card>
  );
};

export default InventoryKPICard;