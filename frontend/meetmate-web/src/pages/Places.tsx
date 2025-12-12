import React from 'react';
import { Box, Typography, Paper } from '@mui/material';

const Places: React.FC = () => {
  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Places
      </Typography>
      <Paper sx={{ p: 3 }}>
        <Typography variant="h6" gutterBottom>
          Place Recommendations
        </Typography>
        <Typography>
          Place recommendations will be displayed here.
        </Typography>
      </Paper>
    </Box>
  );
};

export default Places;
