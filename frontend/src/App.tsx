import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { Container } from '@mui/material';

import Layout from '@/components/Layout';
import HomePage from '@/pages/HomePage';
import TemplatesPage from '@/pages/TemplatesPage';
import TemplateEditorPage from '@/pages/TemplateEditorPage';
import LoginPage from '@/pages/LoginPage';

function App() {
  return (
    <Layout>
      <Container maxWidth="xl" sx={{ mt: 2, mb: 2 }}>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/templates" element={<TemplatesPage />} />
          <Route path="/templates/new" element={<TemplateEditorPage />} />
          <Route path="/templates/:id/edit" element={<TemplateEditorPage />} />
        </Routes>
      </Container>
    </Layout>
  );
}

export default App;