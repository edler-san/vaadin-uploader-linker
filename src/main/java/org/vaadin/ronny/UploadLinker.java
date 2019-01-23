package org.vaadin.ronny;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Upload;

/**
 * Utility class for Vaadin that allows to "link" the Vaadin {@link Upload upload} component to any other {@link AbstractComponent component}.
 * This is useful for custom styled components to provide a standard Vaadin upload functionality or 
 * when you want to trigger the upload functionality from other components based on a user event
 * (which is not possible via server-side calls since the browser blocks non-user originated events).
 * @author Ronny Edler
 *
 */
public class UploadLinker {
	
	/**
	 * Links any {@link AbstractComponent} to an {@link Upload} component. This means that upon a click on the linked component
	 * this click will be "forwarded" to the uploader and trigger its file selector window. Multiple uploaders can be successively
	 * linked to a single upload; multiple components can also be linked to a single uploader. Simply call {@link #link(AbstractComponent, Upload)}
	 * for each component/uploader combination.
	 * 
	 * Known limitations:<ul>
	 * <li> The linking is not permanent. If you reload a page you must reapply it.
	 * <li> Both the component and the uploader <em>must</em> be added towards the view where you want to use them. 
	 * <li> Both the component and the uploader <em>must</em> be visible (i.e., don't use {@link Component#setVisible(boolean)} with <b>false</b> as this removes the component from the DOM.
	 * <li> After the linking the upload component will be made invisible via CSS (style.display='none'). This may affect your layout! So best add the uploader at a convenient space - for instance at the bottom.
	 * <li> The component and the uploader <em>must</em> have their IDs set. See: {@link Upload#setId(String)}, {@link AbstractComponent#setId(String)}}
	 * <li> These IDs <em>must</em> be unique.
	 * <li> In case you build your UI lazily ensure that the components you want to link are present. This method waits an increasing amount of time (25ms, 50ms, 75ms, ...)
	 * for the components to be found and will time out after about 12s. (You can check successful/failed linkings via your browser's console.)
	 * <li> Other registered {@link ClickListener ClickListeners}  will still fire for the source component.
	 * </ul>
	 * @param component Component to receive click event and to forward from.
	 * @param upload Upload component to receive forwarded click event.
	 */
	public static void link(AbstractComponent component, Upload upload) {
		
		String componentId = component.getId();
		if(componentId == null || componentId.equals("")) {
			throw new IllegalArgumentException("Component ID must not be empty.");
		}
		if(!component.isVisible()){
			throw new IllegalArgumentException("Component must be visible.");
		}

		String uploadId = upload.getId();
		if(uploadId == null || uploadId.equals("")) {
			throw new IllegalArgumentException("ID of the uploader must not be empty.");
		}
		if(!upload.isVisible()){
			throw new IllegalArgumentException("Upload must be visible.");
		}
		
		String script = 
		        "const MAX_ATTEMPTS = 30;" +
		        "setTimeout(checkExistence, 25, 1);" +
		        //max running time before failure 11.625s (delays of 25ms, 50ms, ..., 725ms)

		        "function checkExistence(attemptNumber){" +
		          "var component = document.getElementById('"+componentId+"');" + 
		          "var allUploads = document.querySelectorAll('input[type=\"file\"]');" + 
		          "var myUpload;" + 
		          "allUploads.forEach( function(elem){ if(elem.form.id == '"+uploadId+"') myUpload=elem; } );" + 

		          "if (component != null && myUpload != null){ "+
		            "registerClickBridge(component, myUpload);" +
		          "}else{" +
		            " if (attemptNumber < MAX_ATTEMPTS){ "+
		               //increase waiting interval by 25ms
		               "setTimeout(checkExistence, attemptNumber*25, attemptNumber+1);"+
		            "} else {"+
		               "console.warn('Timed out. Could not bind component to uploader. Not all components found in time.');"+
		            "}" +
		          "}"+
		        "};" + //end function checkExistence

		        "function registerClickBridge(component, upload){" +
		          "upload.parentNode.parentNode.style.display='none';"+ //hide uploader widget
		          "component.addEventListener('click', function(){upload.click(); } );" +
		          //"component.onclick = function(){ upload.click(); };"+   //attach click forwarding
		          "console.info('successfully linked component to uploader');" +
		        "}; ";
				
		component.addAttachListener( e -> JavaScript.getCurrent().execute(script));

	}

}
