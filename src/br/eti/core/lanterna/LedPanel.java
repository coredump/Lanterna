package br.eti.core.lanterna;

/*
 * Lanterna - Copyright 2009 José de Paula E. Junior (jose.junior@gmail.com)
 * 
 * This file is part of Lanterna.

 * Lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Lanterna is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Lanterna.  If not, see <http://www.gnu.org/licenses/>.
*/

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;


public class LedPanel extends Drawable {

	int[] local_states;
	int local_width = 0;
	int local_height = 0;

	Paint local_paint = new Paint();
	
	/*
	 * Draws a vertical line of circles in the specified color. The
	 * Width and Height are individual values for each of the circle, not the
	 * total panel width.
	 * 
	 * @param states the array containing the states of the circles (colored or not)
	 * @param width width of the circle
	 * @param height height of the circle
	 * @param color color to paint the circle
	 */
	public LedPanel(int[] states, int width, int height, int color) {
		local_states = states; 
		local_width = width;
		local_height = height;
		local_paint.setColor(color);
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub

		float radius = 0;
		float center_x = 0;
		float center_y = 0;
		
		if (local_width > local_height) {
			radius = local_height / 2;
		} else {
			radius = local_width / 2;
		}
		
		center_x = local_width / 2;
		center_y += local_height / 2;
		
		for(int i = 0; i < local_states.length; i++) {
			if (local_states[i] == 1) {
				canvas.drawCircle(center_x, center_y, radius, local_paint);
			} 
			center_y += local_height;
		}
		
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
		
	}

}
