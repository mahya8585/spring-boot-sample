import React from 'react';
import { 
  Box, 
  CircularProgress, 
  Typography 
} from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme) => ({
  loadingContainer: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: 200,
    padding: theme.spacing(3),
  },
  loadingText: {
    marginTop: theme.spacing(2),
    color: theme.palette.text.secondary,
  },
}));

/**
 * Reusable loading component with consistent styling
 */
const LoadingSpinner = ({ 
  message = '読み込み中...', 
  size = 40,
  minHeight = 200 
}) => {
  const classes = useStyles();

  return (
    <Box 
      className={classes.loadingContainer} 
      style={{ minHeight }}
    >
      <CircularProgress size={size} />
      <Typography 
        variant="body2" 
        className={classes.loadingText}
      >
        {message}
      </Typography>
    </Box>
  );
};

export default LoadingSpinner;