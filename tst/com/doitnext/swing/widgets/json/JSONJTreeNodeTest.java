package com.doitnext.swing.widgets.json;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
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
@RunWith(value = Parameterized.class)
public class JSONJTreeNodeTest {
	 private final String fileName;
	 private final Exception expectedError;
	 
	 public JSONJTreeNodeTest(String fileName, Exception expectedError) {
	    this.fileName = fileName;
	    this.expectedError = expectedError;
	 }

	 @Parameters
	 public static Collection<Object[]> data() {
	   Object[][] data = new Object[][] { 
			   { "array-of-ints.txt", null },
			   { "array-of-strings.txt", null }, 
			   { "empty-array.txt", null }, 
			   { "simple-object.txt", null },
			   { "array-of-arrays.txt", null }};
	   return Arrays.asList(data);
	 }

	 @Test
	 public void pushTest() throws Exception {
		 InputStream resource = null;
		 StringWriter sw = null;
		 try {
			 resource = this.getClass().getResourceAsStream(fileName);
			 sw = new StringWriter();
			 IOUtils.copy(resource, sw);
			 String json = sw.toString();
			 sw.close();
			 sw = null;
			 resource.close();
			 resource = null;
			 JsonElement rootElt = new JsonParser().parse(json);
			 JSONJTreeNode root = new JSONJTreeNode(null, -1, rootElt);
			 
			 JsonElement converted = root.asJsonElement();
			 String expected = rootElt.toString();
			 String actual = converted.toString();
			 
			 Assert.assertEquals("Json does not match", expected, actual);
			 if(expectedError != null)
				 throw new IllegalArgumentException("We didnt' get an " + expectedError.getClass().getName() + " exception as expected.",
					 expectedError);
				 
		 } catch(Exception e) {
			 if(!e.getClass().isInstance(expectedError))
				 throw e;
		 } finally {
			 if(resource != null)
				 resource.close();
			 if(sw != null)
				 sw.close();
		 }
	 }
}
