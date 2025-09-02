import React from 'react';
import { Typography, Box } from '@mui/material';

const TemplateEditorPage: React.FC = () => {
  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Şablon Editörü
      </Typography>
      <Typography variant="body1">
        Drag-and-drop şablon editörü burada olacak. (Görev 9'da implement edilecek)
      </Typography>
    </Box>
  );
};

export default TemplateEditorPage;