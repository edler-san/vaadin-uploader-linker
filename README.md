# vaadin-uploader-linker

Links any _AbstractComponent_ to an _Upload_ component. This means that upon a click on the linked component 
this click will be "forwarded" to the uploader and trigger its file selector window.

Multiple uploaders can be successively linked to a single upload; multiple components can also be linked to a single uploader.
Simply call _link(AbstractComponent, Upload)_ for each component/uploader combination.

Known limitations:

* The linking is not permanent. If you reload a page you must reapply it.
* Both the component and the uploader must be added towards the view where you want to use them.
* Both the component and the uploader must be visible (i.e., don't use Component#setVisible(boolean) with false as this removes the component from the DOM.
* After the linking the upload component will be made invisible via CSS (style.display='none'). This may affect your layout! So best add the uploader at a convenient space - for instance at the bottom
* The component and the uploader must have their IDs set. See: Upload#setId(String), AbstractComponent#setId(String)
These IDs must be unique.
* In case you build your UI lazily ensure that the components you want to link are present. This method waits an increasing amount of time (25ms, 50ms, 75ms, ...)
for the components to be found and will time out after about 12s (30 attempts). (You can check successful/failed linkings via your browser's console.)
* Other registered ClickListeners will still fire for the source and target component.
