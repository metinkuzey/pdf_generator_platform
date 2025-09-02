import React from 'react';
import { AppBar, Toolbar, Typography, Box } from '@mui/material';

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            PDF Generator Platform
          </Typography>
        </Toolbar>
      </AppBar>
      <main>{children}</main>
    </Box>
  );
};

export default Layout;