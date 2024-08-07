package com.ezlifesol.game.snake.obj

import androidx.annotation.Keep
import com.ezlifesol.game.snake.cellNumberX
import com.ezlifesol.game.snake.cellNumberY
import com.ezlifesol.library.gampose.unit.GameSize
import com.ezlifesol.library.gampose.unit.GameVector

/**
 * Class representing the Grass in the game.
 * It contains the size of the grass tiles and a list of grass tile positions.
 */
@Keep
class Grass {
    /**
     * The size of the grass tiles.
     * Initialized to GameSize.zero.
     */
    var size: GameSize = GameSize.zero

    /**
     * Mutable list to store the positions of the grass tiles.
     * Each position is represented by a GameVector.
     */
    val listGrass = mutableListOf<GameVector>()

    /**
     * Companion object for the Grass class.
     * Contains a factory method to create and initialize a Grass object.
     */
    companion object {
        /**
         * Factory method to create and initialize a Grass object.
         * @return a Grass object with initialized grass tile positions.
         */
        fun create(): Grass {
            val grass = Grass()
            for (row in 0 until cellNumberY) {
                if (row % 2 == 0) {
                    // Add grass tiles for even rows
                    for (col in 0 until cellNumberX) {
                        if (col % 2 == 0) {
                            grass.listGrass.add(GameVector(col.toFloat(), row.toFloat()))
                        }
                    }
                }
                if ((row + 1) % 2 == 0) {
                    // Add grass tiles for odd rows
                    for (col in 0 until cellNumberX) {
                        if ((col + 1) % 2 == 0) {
                            grass.listGrass.add(GameVector(col.toFloat(), row.toFloat()))
                        }
                    }
                }
            }
            return grass
        }
    }

    /**
     * Convert the logical position of the grass tile to its actual position based on size.
     * @param position the logical position of the grass tile as a GameVector.
     * @return the actual position of the grass tile as a GameVector, scaled by the size of the tiles.
     */
    fun getGrass(position: GameVector) = GameVector(
        position.x * size.width, position.y * size.height
    )
}
