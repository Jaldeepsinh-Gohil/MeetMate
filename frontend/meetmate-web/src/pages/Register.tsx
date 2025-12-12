import React from 'react';
import { Box, Typography, TextField, Button, Paper } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const Register: React.FC = () => {
  const navigate = useNavigate();

  const handleRegister = () => {
    // TODO: Implement registration logic
    navigate('/login');
  };

  return (
    <Box
      display="flex"
      justifyContent="center"
      alignItems="center"
      minHeight="100vh"
      bgcolor="background.default"
    >
      <Paper elevation={3} sx={{ p: 4, width: '100%', maxWidth: 400 }}>
        <Typography variant="h4" component="h1" gutterBottom align="center">
          MeetMate Register
        </Typography>
        <TextField
          fullWidth
          label="Name"
          margin="normal"
          variant="outlined"
        />
        <TextField
          fullWidth
          label="Email"
          type="email"
          margin="normal"
          variant="outlined"
        />
        <TextField
          fullWidth
          label="Password"
          type="password"
          margin="normal"
          variant="outlined"
        />
        <Button
          fullWidth
          variant="contained"
          color="primary"
          onClick={handleRegister}
          sx={{ mt: 2 }}
        >
          Register
        </Button>
      </Paper>
    </Box>
  );
};

export default Register;
