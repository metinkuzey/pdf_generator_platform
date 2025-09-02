import React from 'react';
import { Typography, Box } from '@mui/material';

const TemplatesPage: React.FC = () => {
  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Şablonlar
      </Typography>
      <Typography variant="body1">
        Şablon listesi burada görüntülenecek. (Görev 3'te implement edilecek)
      </Typography>
    </Box>
  );
};

export default TemplatesPage;