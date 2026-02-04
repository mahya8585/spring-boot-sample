import React from 'react';
import {
  Paper,
  Typography,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Chip,
  Box,
  CircularProgress,
  Divider
} from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import {
  TrendingUp,
  TrendingDown,
  Storage,
  Error,
  Info
} from '@material-ui/icons';

const useStyles = makeStyles((theme) => ({
  paper: {
    padding: theme.spacing(2),
    height: 400,
    overflow: 'auto',
  },
  loading: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: 300,
  },
  title: {
    marginBottom: theme.spacing(2),
    fontWeight: 'bold',
  },
  alertItem: {
    borderRadius: theme.spacing(1),
    marginBottom: theme.spacing(1),
    '&:hover': {
      backgroundColor: theme.palette.action.hover,
    },
  },
  highSeverity: {
    borderLeft: `4px solid ${theme.palette.error.main}`,
  },
  mediumSeverity: {
    borderLeft: `4px solid ${theme.palette.warning.main}`,
  },
  lowSeverity: {
    borderLeft: `4px solid ${theme.palette.info.main}`,
  },
  alertText: {
    fontSize: '0.9rem',
  },
  alertAction: {
    fontSize: '0.8rem',
    color: theme.palette.text.secondary,
    marginTop: theme.spacing(0.5),
  },
  emptyState: {
    textAlign: 'center',
    color: theme.palette.text.secondary,
    padding: theme.spacing(3),
  },
}));

const AlertsPanel = ({ alerts, loading }) => {
  const classes = useStyles();

  const getAlertIcon = (alertType) => {
    switch (alertType) {
      case 'RISING':
        return <TrendingUp style={{ color: '#4caf50' }} />;
      case 'FALLING':
        return <TrendingDown style={{ color: '#f44336' }} />;
      case 'LOW_STOCK':
        return <Storage style={{ color: '#ff9800' }} />;
      case 'DEAD_STOCK':
        return <Error style={{ color: '#f44336' }} />;
      default:
        return <Info style={{ color: '#2196f3' }} />;
    }
  };

  const getSeverityChip = (severity) => {
    const colors = {
      HIGH: { bg: '#f44336', text: 'white' },
      MEDIUM: { bg: '#ff9800', text: 'white' },
      LOW: { bg: '#2196f3', text: 'white' },
    };
    
    const color = colors[severity] || colors.LOW;
    
    return (
      <Chip
        label={severity}
        size="small"
        style={{
          backgroundColor: color.bg,
          color: color.text,
          fontSize: '0.75rem',
          fontWeight: 'bold',
        }}
      />
    );
  };

  const getSeverityClass = (severity) => {
    switch (severity) {
      case 'HIGH':
        return classes.highSeverity;
      case 'MEDIUM':
        return classes.mediumSeverity;
      case 'LOW':
        return classes.lowSeverity;
      default:
        return classes.lowSeverity;
    }
  };

  if (loading) {
    return (
      <Paper className={classes.paper}>
        <div className={classes.loading}>
          <CircularProgress size={40} />
        </div>
      </Paper>
    );
  }

  return (
    <Paper className={classes.paper}>
      <Typography variant="h6" className={classes.title}>
        重要アラート
      </Typography>
      
      {!alerts || alerts.length === 0 ? (
        <Box className={classes.emptyState}>
          <Info style={{ fontSize: 48, marginBottom: 16, opacity: 0.3 }} />
          <Typography variant="body2">
            現在、アラートはありません
          </Typography>
        </Box>
      ) : (
        <List>
          {alerts.map((alert, index) => (
            <React.Fragment key={index}>
              <ListItem 
                className={`${classes.alertItem} ${getSeverityClass(alert.severity)}`}
              >
                <ListItemIcon>
                  {getAlertIcon(alert.alertType)}
                </ListItemIcon>
                <ListItemText
                  primary={
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                      <Typography className={classes.alertText}>
                        {alert.message}
                      </Typography>
                      {getSeverityChip(alert.severity)}
                    </Box>
                  }
                  secondary={
                    <Box>
                      <Typography className={classes.alertAction}>
                        カテゴリ: {alert.techCategory}
                      </Typography>
                      <Typography className={classes.alertAction}>
                        対応: {alert.actionRequired}
                      </Typography>
                      {alert.changePercent && (
                        <Typography className={classes.alertAction}>
                          変動: {alert.changePercent > 0 ? '+' : ''}{alert.changePercent}%
                        </Typography>
                      )}
                    </Box>
                  }
                />
              </ListItem>
              {index < alerts.length - 1 && <Divider />}
            </React.Fragment>
          ))}
        </List>
      )}
    </Paper>
  );
};

export default AlertsPanel;