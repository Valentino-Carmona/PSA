import React, { Suspense } from 'react';
import { Box, Typography, Container, CircularProgress } from '@mui/material';
import { useSuspenseQuery } from '@tanstack/react-query';
import { apiClient } from './utils/apiClient';

// Componente de prueba para conexión
const ProjectsConnectionTest: React.FC = () => {
  const { data } = useSuspenseQuery({
    queryKey: ['projects'],
    queryFn: async () => {
      const response = await apiClient.get('/proyectos');
      return response.data;
    },
  });

  return (
    <Box sx={{ mt: 4, p: 2, bgcolor: '#111115', border: '1px solid #2a2a32', borderRadius: 1, textAlign: 'left' }}>
      <Typography variant="overline" color="primary">Conexión Exitosa - Datos de Proyectos:</Typography>
      <pre style={{ margin: 0, marginTop: '1rem', whiteSpace: 'pre-wrap', fontFamily: 'JetBrains Mono, monospace', fontSize: '0.8rem', color: '#8a8a96' }}>
        {JSON.stringify(data, null, 2)}
      </pre>
    </Box>
  );
};

const App: React.FC = () => {
  return (
    <Container maxWidth="md" sx={{ mt: 8, textAlign: 'center' }}>
      <Box sx={{ mb: 6 }}>
        <Typography variant="h1" gutterBottom color="primary">
          CASA CARMONA
        </Typography>
        <Typography variant="overline" sx={{ display: 'block' }} gutterBottom>
          SICVT LVCIFER LVCET IN AVRORA • ITA EST GENS CARMONA
        </Typography>
      </Box>
      
      <Box sx={{ p: 4, bgcolor: 'background.paper', borderRadius: 1, border: '1px solid', borderColor: 'divider' }}>
        <Typography variant="h4" gutterBottom>
          Sistema de Gestión Unificada
        </Typography>
        <Typography variant="body1" sx={{ color: 'text.secondary' }}>
          Prueba de conexión con el Backend (Spring Boot).
        </Typography>
        
        <Suspense fallback={<Box sx={{ mt: 4 }}><CircularProgress color="primary" /></Box>}>
          <ProjectsConnectionTest />
        </Suspense>
      </Box>
    </Container>
  );
};

export default App;
