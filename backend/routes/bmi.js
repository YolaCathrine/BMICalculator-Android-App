import express from 'express';
import { authenticateToken } from '../middleware/auth.js';
import pool from '../db.js';

const router = express.Router();

// Calculate BMI category
const getBMICategory = (bmi) => {
  if (bmi < 18.5) return 'Underweight';
  if (bmi < 25) return 'Normal weight';
  if (bmi < 30) return 'Overweight';
  return 'Obese';
};

// Get all BMI records for user
router.get('/', authenticateToken, async (req, res) => {
  try {
    const result = await pool.query(
      'SELECT * FROM bmi_records WHERE user_id = $1 ORDER BY recorded_at DESC',
      [req.user.userId]
    );
    res.json({ records: result.rows });
  } catch (error) {
    console.error('Get records error:', error);
    res.status(500).json({ error: 'Failed to fetch records' });
  }
});

// Get single BMI record
router.get('/:id', authenticateToken, async (req, res) => {
  try {
    const result = await pool.query(
      'SELECT * FROM bmi_records WHERE id = $1 AND user_id = $2',
      [req.params.id, req.user.userId]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Record not found' });
    }

    res.json({ record: result.rows[0] });
  } catch (error) {
    console.error('Get record error:', error);
    res.status(500).json({ error: 'Failed to fetch record' });
  }
});

// Create new BMI record
router.post('/', authenticateToken, async (req, res) => {
  const { weight, height } = req.body;

  if (!weight || !height) {
    return res.status(400).json({ error: 'Weight and height are required' });
  }

  try {
    // Calculate BMI: weight (kg) / (height (m))^2
    const heightInMeters = height / 100;
    const bmiValue = (weight / (heightInMeters * heightInMeters)).toFixed(2);
    const category = getBMICategory(parseFloat(bmiValue));

    const result = await pool.query(
      'INSERT INTO bmi_records (user_id, weight, height, bmi_value, category) VALUES ($1, $2, $3, $4, $5) RETURNING *',
      [req.user.userId, weight, height, bmiValue, category]
    );

    res.status(201).json({
      message: 'BMI record created successfully',
      record: result.rows[0]
    });
  } catch (error) {
    console.error('Create record error:', error);
    res.status(500).json({ error: 'Failed to create record' });
  }
});

// Delete BMI record
router.delete('/:id', authenticateToken, async (req, res) => {
  try {
    const result = await pool.query(
      'DELETE FROM bmi_records WHERE id = $1 AND user_id = $2 RETURNING *',
      [req.params.id, req.user.userId]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Record not found' });
    }

    res.json({ message: 'Record deleted successfully' });
  } catch (error) {
    console.error('Delete record error:', error);
    res.status(500).json({ error: 'Failed to delete record' });
  }
});

// Get BMI history with stats
router.get('/stats/history', authenticateToken, async (req, res) => {
  try {
    const result = await pool.query(
      `SELECT 
        DATE(recorded_at) as date,
        AVG(bmi_value) as avg_bmi,
        AVG(weight) as avg_weight,
        COUNT(*) as count
       FROM bmi_records 
       WHERE user_id = $1 
       GROUP BY DATE(recorded_at) 
       ORDER BY date DESC 
       LIMIT 30`,
      [req.user.userId]
    );

    res.json({ history: result.rows });
  } catch (error) {
    console.error('Get stats error:', error);
    res.status(500).json({ error: 'Failed to fetch stats' });
  }
});

export default router;
