import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Card,
  CardContent,
  Button,
  Grid,
  CircularProgress,
  Paper
} from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import api from '../services/api';

const useStyles = makeStyles((theme) => ({
  errorAlert: {
    backgroundColor: theme.palette.error.light,
    color: theme.palette.error.contrastText,
    padding: theme.spacing(2),
    marginBottom: theme.spacing(2),
    borderRadius: theme.shape.borderRadius,
  },
}));

const TestComponent = () => {
  const classes = useStyles();
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchBooks = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.get('/api/v1/books');
      setBooks(response.data.content || []);
    } catch (err) {
      setError('書籍データの取得に失敗しました: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBooks();
  }, []);

  return (
    <Container maxWidth="md" style={{ marginTop: 20 }}>
      <Typography variant="h4" gutterBottom>
        TechBookStore - 動作テスト
      </Typography>
      
      <Card style={{ marginBottom: 20 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            API接続テスト
          </Typography>
          <Button 
            variant="contained" 
            color="primary" 
            onClick={fetchBooks}
            disabled={loading}
          >
            {loading ? <CircularProgress size={20} /> : '書籍データを取得'}
          </Button>
        </CardContent>
      </Card>

      {error && (
        <Paper className={classes.errorAlert}>
          <Typography variant="body1">
            {error}
          </Typography>
        </Paper>
      )}

      {books.length > 0 && (
        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              取得した書籍データ (最初の3件)
            </Typography>
            <Grid container spacing={2}>
              {books.slice(0, 3).map((book) => (
                <Grid item xs={12} md={4} key={book.id}>
                  <Card variant="outlined">
                    <CardContent>
                      <Typography variant="subtitle1" gutterBottom>
                        {book.title}
                      </Typography>
                      <Typography variant="body2" color="textSecondary">
                        ISBN: {book.isbn13}
                      </Typography>
                      <Typography variant="body2" color="textSecondary">
                        価格: ¥{book.sellingPrice}
                      </Typography>
                      <Typography variant="body2" color="textSecondary">
                        レベル: {book.level}
                      </Typography>
                    </CardContent>
                  </Card>
                </Grid>
              ))}
            </Grid>
          </CardContent>
        </Card>
      )}
    </Container>
  );
};

export default TestComponent;
