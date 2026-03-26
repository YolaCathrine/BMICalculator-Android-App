import express from 'express';
import { authenticateToken } from '../middleware/auth.js';
import pool from '../db.js';

const router = express.Router();

// Get all goals for user
router.get('/', authenticateToken, async (req, res) => {
  try {
    const result = await pool.query(
      'SELECT * FROM goals WHERE user_id = $1 ORDER BY created_at DESC',
      [req.user.userId]
    );
    res.json({ goals: result.rows });
  } catch (error) {
    console.error('Get goals error:', error);
    res.status(500).json({ error: 'Failed to fetch goals' });
  }
});

// Get single goal
router.get('/:id', authenticateToken, async (req, res) => {
  try {
    const result = await pool.query(
      'SELECT * FROM goals WHERE id = $1 AND user_id = $2',
      [req.params.id, req.user.userId]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Goal not found' });
    }

    res.json({ goal: result.rows[0] });
  } catch (error) {
    console.error('Get goal error:', error);
    res.status(500).json({ error: 'Failed to fetch goal' });
  }
});

// Create new goal
router.post('/', authenticateToken, async (req, res) => {
  const { target_weight, target_bmi, goal_date, current_weight } = req.body;

  if (!target_weight) {
    return res.status(400).json({ error: 'Target weight is required' });
  }

  try {
    const result = await pool.query(
      'INSERT INTO goals (user_id, target_weight, target_bmi, goal_date, current_weight) VALUES ($1, $2, $3, $4, $5) RETURNING *',
      [req.user.userId, target_weight, target_bmi || null, goal_date || null, current_weight || null]
    );

    res.status(201).json({
      message: 'Goal created successfully',
      goal: result.rows[0]
    });
  } catch (error) {
    console.error('Create goal error:', error);
    res.status(500).json({ error: 'Failed to create goal' });
  }
});

// Update goal
router.put('/:id', authenticateToken, async (req, res) => {
  const { target_weight, target_bmi, goal_date, current_weight, status } = req.body;

  try {
    const result = await pool.query(
      `UPDATE goals 
       SET target_weight = COALESCE($1, target_weight),
           target_bmi = COALESCE($2, target_bmi),
           goal_date = COALESCE($3, goal_date),
           current_weight = COALESCE($4, current_weight),
           status = COALESCE($5, status)
       WHERE id = $6 AND user_id = $7
       RETURNING *`,
      [target_weight, target_bmi, goal_date, current_weight, status, req.params.id, req.user.userId]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Goal not found' });
    }

    res.json({
      message: 'Goal updated successfully',
      goal: result.rows[0]
    });
  } catch (error) {
    console.error('Update goal error:', error);
    res.status(500).json({ error: 'Failed to update goal' });
  }
});

// Delete goal
router.delete('/:id', authenticateToken, async (req, res) => {
  try {
    const result = await pool.query(
      'DELETE FROM goals WHERE id = $1 AND user_id = $2 RETURNING *',
      [req.params.id, req.user.userId]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Goal not found' });
    }

    res.json({ message: 'Goal deleted successfully' });
  } catch (error) {
    console.error('Delete goal error:', error);
    res.status(500).json({ error: 'Failed to delete goal' });
  }
});

export default router;
