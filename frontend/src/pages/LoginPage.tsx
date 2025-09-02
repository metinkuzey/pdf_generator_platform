import React from 'react';
import { Typography, Box } from '@mui/material';

const LoginPage: React.FC = () => {
  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Giriş Yap
      </Typography>
      <Typography variant="body1">
        Giriş formu burada olacak. (Görev 11'de implement edilecek)
      </Typography>
    </Box>
  );
};

export default LoginPage;