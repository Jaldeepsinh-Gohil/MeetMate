import React from 'react';
import { Box, Typography, Paper } from '@mui/material';

const GroupDetail: React.FC = () => {
  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Group Details
      </Typography>
      <Paper sx={{ p: 3 }}>
        <Typography variant="h6" gutterBottom>
          Group Information
        </Typography>
        <Typography>
          Group details will be displayed here.
        </Typography>
      </Paper>
    </Box>
  );
};

export default GroupDetail;
