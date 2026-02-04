import React from 'react';
import { 
  Snackbar,
  IconButton 
} from '@material-ui/core';
import { 
  Alert
} from '@material-ui/lab';
import { Close as CloseIcon } from '@material-ui/icons';

/**
 * Enhanced error notification component with better UX
 */
const ErrorNotification = ({ 
  open, 
  message, 
  severity = 'error', 
  onClose,
  autoHideDuration = 6000 
}) => {
  return (
    <Snackbar
      anchorOrigin={{
        vertical: 'top',
        horizontal: 'right',
      }}
      open={open}
      autoHideDuration={autoHideDuration}
      onClose={onClose}
    >
      <Alert 
        severity={severity}
        action={
          <IconButton
            aria-label="close"
            color="inherit"
            size="small"
            onClick={onClose}
          >
            <CloseIcon fontSize="inherit" />
          </IconButton>
        }
      >
        {message}
      </Alert>
    </Snackbar>
  );
};

export default ErrorNotification;