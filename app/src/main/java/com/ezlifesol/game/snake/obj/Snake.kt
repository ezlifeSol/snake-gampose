package com.ezlifesol.game.snake.obj

import androidx.annotation.Keep
import com.ezlifesol.game.snake.cellNumberX
import com.ezlifesol.game.snake.cellNumberY
import com.ezlifesol.library.gampose.unit.GameSize
import com.ezlifesol.library.gampose.unit.GameVector
import kotlin.math.round

/**
 * Class representing the Snake in the game.
 * It contains properties and methods to manage the snake's movement, body segments, and sprites.
 */
@Keep
class Snake {
    // Image assets for the snake's head in different directions
    private val headUp = "head_up.webp"
    private val headDown = "head_down.webp"
    private val headLeft = "head_left.webp"
    private val headRight = "head_right.webp"

    // Image assets for the snake's body in different orientations
    private val bodyHorizontal = "body_horizontal.webp"
    private val bodyVertical = "body_vertical.webp"
    private val bodyTL = "body_tl.webp"  // Top-left corner
    private val bodyTR = "body_tr.webp"  // Top-right corner
    private val bodyBL = "body_bl.webp"  // Bottom-left corner
    private val bodyBR = "body_br.webp"  // Bottom-right corner

    // Image assets for the snake's tail in different directions
    private val tailUp = "tail_up.webp"
    private val tailDown = "tail_down.webp"
    private val tailLeft = "tail_left.webp"
    private val tailRight = "tail_right.webp"

    /**
     * The size of the snake's segments.
     * Initialized to GameSize.zero.
     */
    var size: GameSize = GameSize.zero

    /**
     * The current direction of the snake's movement.
     * Initialized to move right.
     */
    var direction: GameVector = GameVector(1f, 0f)

    /**
     * Flag to indicate whether a new block should be added to the snake's body.
     */
    private var newBlock = false

    /**
     * List of positions representing the snake's body segments.
     * Initialized to a three-block snake starting at the center of the grid.
     */
    var listBody = mutableListOf(
        GameVector(round(cellNumberX / 2f) - 1, round(cellNumberY / 2f)),
        GameVector(round(cellNumberX / 2f), round(cellNumberY / 2f)),
        GameVector(round(cellNumberX / 2f) + 1, round(cellNumberY / 2f)),
    )

    /**
     * Flag to indicate whether the game is over.
     */
    var isGameOver = false
        private set

    /**
     * Companion object for the Snake class.
     * Contains a factory method to create and initialize a Snake object.
     */
    companion object {
        /**
         * Factory method to create and initialize a Snake object.
         * @return a Snake object with initialized body segments.
         */
        fun create(): Snake {
            val snake = Snake()
            return snake
        }
    }

    /**
     * Convert the logical position of a body segment to its actual position based on size.
     * @param position the logical position of the body segment as a GameVector.
     * @return the actual position of the body segment as a GameVector, scaled by the size of the segments.
     */
    fun getBody(position: GameVector) = GameVector(
        position.x * size.width, position.y * size.height
    )

    /**
     * Move the snake by updating the positions of its body segments based on the current direction.
     * If newBlock is true, a new segment is added to the snake's body.
     * Otherwise, the snake moves forward by one segment.
     */
    fun moveSnake() {
        if (isGameOver) return // Do nothing if the game is over

        val bodyCopy = listBody.toMutableList()
        if (!newBlock) {
            bodyCopy.removeFirst()  // Remove the tail segment if no new block is added
        }
        val newHead = bodyCopy.last() + direction

        // Wrap the snake around if it hits a wall
        newHead.x = when {
            newHead.x < 0 -> (cellNumberX - 1).toFloat()
            newHead.x >= cellNumberX -> 0f
            else -> newHead.x
        }
        newHead.y = when {
            newHead.y < 0 -> (cellNumberY - 1).toFloat()
            newHead.y >= cellNumberY -> 0f
            else -> newHead.y
        }

        bodyCopy.add(newHead)  // Add the new head segment in the current direction
        listBody = bodyCopy
        newBlock = false

        // Check for self-collision
        checkSelfCollision()
    }

    /**
     * Add a new block to the snake's body in the next move.
     */
    fun addBlock() {
        newBlock = true
    }

    /**
     * Check if the snake's head collides with any of its body segments.
     * If so, set the game over flag to true.
     */
    private fun checkSelfCollision() {
        val headPosition = listBody.last()
        if (listBody.dropLast(1).any { it == headPosition }) {
            isGameOver = true
        }
    }

    /**
     * Reset the snake to its initial state and set game over flag to false.
     */
    fun reset() {
        // Reset snake size and direction
        size = GameSize.zero
        direction = GameVector(1f, 0f)

        // Reset the body to the initial state
        listBody = mutableListOf(
            GameVector(round(cellNumberX / 2f) - 1, round(cellNumberY / 2f)),
            GameVector(round(cellNumberX / 2f), round(cellNumberY / 2f)),
            GameVector(round(cellNumberX / 2f) + 1, round(cellNumberY / 2f)),
        )

        // Reset flags
        newBlock = false
        isGameOver = false
    }

    /**
     * Get the appropriate sprite for the snake's head based on its current direction.
     * @return the file name of the head sprite.
     */
    fun headSprite() = when {
        direction.x == 1f -> headRight
        direction.x == -1f -> headLeft
        direction.y == 1f -> headDown
        direction.y == -1f -> headUp
        else -> headRight
    }

    /**
     * Get the appropriate sprite for the snake's tail based on its relation to the adjacent segment.
     * @return the file name of the tail sprite.
     */
    fun tailSprite(): String {
        val tailRelation = listBody.first() - listBody[1]
        return when {
            tailRelation.x == 1f -> tailRight
            tailRelation.x == -1f -> tailLeft
            tailRelation.y == 1f -> tailDown
            tailRelation.y == -1f -> tailUp
            else -> tailRight
        }
    }

    /**
     * Get the appropriate sprite for a body segment based on its relation to adjacent segments.
     * @param index the index of the body segment in the list.
     * @param body the position of the body segment as a GameVector.
     * @return the file name of the body sprite.
     */
    fun bodySprite(index: Int, body: GameVector): String {
        val prevBlock = listBody[index + 1] - body
        val nextBlock = listBody[index - 1] - body
        return if (prevBlock.x == nextBlock.x) {
            bodyVertical
        } else if (prevBlock.y == nextBlock.y) {
            bodyHorizontal
        } else {
            if ((prevBlock.x == -1f && nextBlock.y == -1f) || (prevBlock.y == -1f && nextBlock.x == -1f)) {
                bodyTL
            } else if ((prevBlock.x == -1f && nextBlock.y == 1f) || (prevBlock.y == 1f && nextBlock.x == -1f)) {
                bodyBL
            } else if ((prevBlock.x == 1f && nextBlock.y == -1f) || (prevBlock.y == -1f && nextBlock.x == 1f)) {
                bodyTR
            } else {
                bodyBR
            }
        }
    }
}
