Parse.Cloud.define("initializeNewUser", function (request, response) {
    console.log('Start initializeNewUser');
    var user = request.user;
    console.log('Found new user: ' + user.get("name"));
    var ACL = new Parse.ACL(user);
    var unsavedItemObjects = [];

    var attributes = new Parse.Object("ListAttributes");
    attributes.set('author', user);
    attributes.set('listAttributesID', 1);
    attributes.set('name', 'Genoa');
    attributes.set('nameLowercase', 'genoa');
    attributes.set("isDefaultAttributes", false);
    attributes.set("isChecked", false);
    attributes.set("startColor", -11761266);
    attributes.set("endColor", -15576746);
    attributes.set("textColor", -1);
    attributes.set("textSize", 15);
    attributes.set("isBold", true);
    attributes.set("horizontalPaddingInDp", 5);
    attributes.set("verticalPaddingInDp", 10);
    attributes.set("isTransparent", false);
    attributes.set("localUuid", '');
    attributes.set("isAttributesDirty", false);
    attributes.set("isMarkedForDeletion", false);
    attributes.setACL(ACL);
    unsavedItemObjects[0] = attributes;

    var attributes = new Parse.Object("ListAttributes");
    attributes.set('author', user);
    attributes.set('listAttributesID', 2);
    attributes.set('name', 'Opal');
    attributes.set('nameLowercase', 'opal');
    attributes.set("isDefaultAttributes", false);
    attributes.set("isChecked", false);
    attributes.set("startColor", -3416876);
    attributes.set("endColor", -7231843);
    attributes.set("textColor", -16777216);
    attributes.set("textSize", 17);
    attributes.set("isBold", false);
    attributes.set("horizontalPaddingInDp", 10);
    attributes.set("verticalPaddingInDp", 15);
    attributes.set("isTransparent", false);
    attributes.set("localUuid", '');
    attributes.set("isAttributesDirty", false);
    attributes.set("isMarkedForDeletion", false);
    attributes.setACL(ACL);
    unsavedItemObjects[1] = attributes;

    var attributes = new Parse.Object("ListAttributes");
    attributes.set('author', user);
    attributes.set('listAttributesID', 3);
    attributes.set('name', 'Shades of Blue');
    attributes.set('nameLowercase', 'shades of blue');
    attributes.set("isDefaultAttributes", false);
    attributes.set("isChecked", false);
    attributes.set("startColor", -5777934);
    attributes.set("endColor", -10841921);
    attributes.set("textColor", -16777216);
    attributes.set("textSize", 17);
    attributes.set("isBold", false);
    attributes.set("horizontalPaddingInDp", 10);
    attributes.set("verticalPaddingInDp", 15);
    attributes.set("isTransparent", false);
    attributes.set("localUuid", '');
    attributes.set("isAttributesDirty", false);
    attributes.set("isMarkedForDeletion", false);
    attributes.setACL(ACL);
    unsavedItemObjects[2] = attributes;

    var attributes = new Parse.Object("ListAttributes");
    attributes.set('author', user);
    attributes.set('listAttributesID', 4);
    attributes.set('name', 'Off White');
    attributes.set('nameLowercase', 'off white');
    attributes.set("isDefaultAttributes", false);
    attributes.set("isChecked", false);
    attributes.set("startColor", -1);
    attributes.set("endColor", -2436147);
    attributes.set("textColor", -16777216);
    attributes.set("textSize", 17);
    attributes.set("isBold", false);
    attributes.set("horizontalPaddingInDp", 10);
    attributes.set("verticalPaddingInDp", 1);
    attributes.set("isTransparent", true);
    attributes.set("localUuid", '');
    attributes.set("isAttributesDirty", false);
    attributes.set("isMarkedForDeletion", false);
    attributes.setACL(ACL);
    unsavedItemObjects[3] = attributes;


    //var attributes = new Parse.Object("ListAttributes");
    //attributes.set('author', user);\
    //    attributes.set('listAttributesID', 5);
    //attributes.set('name', 'Genoa');
    //attributes.set('nameLowercase', 'genoa');
    //attributes.set("isDefaultAttributes", false);
    //attributes.set("isChecked", false);
    //attributes.set("startColor", );
    //attributes.set("endColor", );
    //attributes.set("textColor", -16777216);
    //attributes.set("textSize", 17);
    //attributes.set("isBold", false);
    //attributes.set("horizontalPaddingInDp", 10);
    //attributes.set("verticalPaddingInDp", 15);
    //attributes.set("isTransparent", false);
    //attributes.set("localUuid", '');
    //attributes.set("isAttributesDirty", false);
    //attributes.set("isMarkedForDeletion", false);
    //attributes.setACL(ACL);
    //unsavedItemObjects[4] = attributes;
    //
    //var attributes = new Parse.Object("ListAttributes");
    //attributes.set('author', user);
    //    attributes.set('listAttributesID', 6);
    //attributes.set('name', 'Genoa');
    //attributes.set('nameLowercase', 'genoa');
    //attributes.set("isDefaultAttributes", false);
    //attributes.set("isChecked", false);
    //attributes.set("startColor", );
    //attributes.set("endColor", );
    //attributes.set("textColor", -16777216);
    //attributes.set("textSize", 17);
    //attributes.set("isBold", false);
    //attributes.set("horizontalPaddingInDp", 10);
    //attributes.set("verticalPaddingInDp", 15);
    //attributes.set("isTransparent", false);
    //attributes.set("localUuid", '');
    //attributes.set("isAttributesDirty", false);
    //attributes.set("isMarkedForDeletion", false);
    //attributes.setACL(ACL);
    //unsavedItemObjects[5] = attributes;


    console.log('Ready to save ' + unsavedItemObjects.length + ' Attributes for user: ' + user.get("name"));

    Parse.Object.saveAll(unsavedItemObjects, {
        success: function (list) {
            // All the objects were saved.
            console.log('Successfully initialized ' + list.length + ' Attributes for user: ' + user.get("name"));
            response.success(list.length);
        },
        error: function (error) {
            // An error occurred while saving one of the objects.
            console.log('An error occurred while saving one of the Attribute objects. Error code: ' + error.code + ' ' + error.message);
            response.error('An error occurred while saving one of the Attribute objects. Error code: ' + error.code + ' ' + error.message);
        }
    });
});


