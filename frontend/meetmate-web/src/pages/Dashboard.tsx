import React from 'react';
import { Box, Typography, Button, Paper } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const Dashboard: React.FC = () => {
  const navigate = useNavigate();

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        MeetMate Dashboard
      </Typography>
      <Paper sx={{ p: 3 }}>
        <Typography variant="h6" gutterBottom>
          Welcome to MeetMate!
        </Typography>
        <Button
          variant="contained"
          onClick={() => navigate('/groups')}
          sx={{ mr: 2 }}
        >
          Manage Groups
        </Button>
        <Button
          variant="contained"
          onClick={() => navigate('/places')}
        >
          Find Places
        </Button>
      </Paper>
    </Box>
  );
};

export default Dashboard;
