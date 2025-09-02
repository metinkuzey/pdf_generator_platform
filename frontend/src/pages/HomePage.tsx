import React from 'react';
import { Typography, Box, Button, Grid, Card, CardContent } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { Add, ViewList, Dashboard } from '@mui/icons-material';

const HomePage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <Box>
      <Typography variant="h3" component="h1" gutterBottom>
        PDF Generator Platform'a Hoş Geldiniz
      </Typography>
      
      <Typography variant="h6" color="text.secondary" paragraph>
        Profesyonel PDF şablonları oluşturun ve dinamik verilerle PDF'ler üretin.
      </Typography>

      <Grid container spacing={3} sx={{ mt: 4 }}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" mb={2}>
                <Add color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Yeni Şablon</Typography>
              </Box>
              <Typography variant="body2" color="text.secondary" paragraph>
                Drag-and-drop arayüzü ile yeni PDF şablonları oluşturun.
              </Typography>
              <Button 
                variant="contained" 
                onClick={() => navigate('/templates/new')}
                fullWidth
              >
                Şablon Oluştur
              </Button>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" mb={2}>
                <ViewList color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Şablonlar</Typography>
              </Box>
              <Typography variant="body2" color="text.secondary" paragraph>
                Mevcut şablonları görüntüleyin ve düzenleyin.
              </Typography>
              <Button 
                variant="outlined" 
                onClick={() => navigate('/templates')}
                fullWidth
              >
                Şablonları Görüntüle
              </Button>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" mb={2}>
                <Dashboard color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Dashboard</Typography>
              </Box>
              <Typography variant="body2" color="text.secondary" paragraph>
                Sistem metrikleri ve raporları görüntüleyin.
              </Typography>
              <Button 
                variant="outlined" 
                disabled
                fullWidth
              >
                Yakında
              </Button>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default HomePage;