package com.doitnext.swing.widgets.json;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Launches an instance of JSONEditPanel for testing.
 * 
 * @author Stephen Owens
 *
 *
 * <p>
 * Copyright 2011 Stephen P. Owens : steve@doitnext.com
 * </p>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * </p>
 * <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
public class JSONEditDriver implements OKCancelListener {
	private JSONEditFrame theFrame; 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new JSONEditDriver();		
	}

	private JSONEditDriver() {
		theFrame = new JSONEditFrame(this, "Testing JSONEditFrame", "{}");
	}
	
	@Override
	public void onFrameAction(Action action, JFrame frame) {
		if(theFrame == frame) {
			String json = theFrame.getJson();
			theFrame.closeWindow();
			if(action.equals(Action.OK)) {
				JOptionPane.showMessageDialog(null,
						 json, "Resulting JSON", JOptionPane.INFORMATION_MESSAGE);
			} else if(action.equals(Action.DEFAULT_CLOSE)) {
				JOptionPane.showMessageDialog(null,
						 "The default close button was clicked", "User Hit Default Close", JOptionPane.INFORMATION_MESSAGE);
			} else{
				JOptionPane.showMessageDialog(null,
						 "The cancel button was clicked", "User Hit Cancel", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

}
