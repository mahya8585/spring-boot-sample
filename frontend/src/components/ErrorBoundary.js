import React from 'react';
import { Alert, AlertTitle } from '@material-ui/lab';
import { Box, Button } from '@material-ui/core';
import { Refresh } from '@material-ui/icons';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null, errorInfo: null };
  }

  static getDerivedStateFromError() {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    this.setState({
      error,
      errorInfo
    });
  }

  handleRetry = () => {
    this.setState({ hasError: false, error: null, errorInfo: null });
  };

  render() {
    if (this.state.hasError) {
      return (
        <Box p={3}>
          <Alert severity="error">
            <AlertTitle>エラーが発生しました</AlertTitle>
            レポートの読み込み中にエラーが発生しました。ページを再読み込みしてください。
            <Box mt={2}>
              <Button
                variant="contained"
                color="primary"
                startIcon={<Refresh />}
                onClick={this.handleRetry}
              >
                再試行
              </Button>
            </Box>
          </Alert>
          {process.env.NODE_ENV === 'development' && (
            <Box mt={2}>
              <details>
                <summary>開発者向け詳細情報</summary>
                <pre style={{ fontSize: '12px', color: '#666' }}>
                  {this.state.error && this.state.error.toString()}
                  <br />
                  {this.state.errorInfo.componentStack}
                </pre>
              </details>
            </Box>
          )}
        </Box>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;