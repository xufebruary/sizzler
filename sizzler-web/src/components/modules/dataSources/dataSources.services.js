'use strict';

DataSourcesServices.$inject = ['DataSourceResources'];

function DataSourcesServices(DataSourceResources){
    this.DataSourceResources = DataSourceResources;
}

DataSourcesServices.prototype={
    constructor: DataSourcesServices,

    getTimezone: function(linkInfo){
        return this.DataSourceResources.getTimezone(null, linkInfo);
    },

    updateTimezone: function(sendData, linkInfo){
        return this.DataSourceResources.updateTimezone(sendData, linkInfo);
    }
}
export default DataSourcesServices;