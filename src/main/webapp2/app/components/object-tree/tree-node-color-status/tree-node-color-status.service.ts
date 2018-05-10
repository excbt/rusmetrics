import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { HttpClient, HttpParams } from '@angular/common/http';

import { SERVER_API_URL } from '../../../app.constants';

import { TreeNodeColorStatus } from './';
// import { TreeNavigateService } from './tree-navigate/tree-navigate.service';setSelectedPTreeNodeId

@Injectable()
export class TreeNodeColorStatusService {

//        var COMMON_DATA_URL = "../api/p-tree-node-monitor/node-color-status/{nodeId}",
//        NODE_STATUS_DETAILS_URL = "../api/p-tree-node-monitor/node-color-status/{nodeId}/status-details/{levelColorKeyname}";

//        private COMMON_DATA_URL: string = SERVER_API_URL +'api/p-tree-node-monitor/node-color-status/{nodeId}'
//        private NODE_STATUS_DETAILS_URL: string = SERVER_API_URL + '../api/p-tree-node-monitor/node-color-status/{nodeId}/status-details/{levelColorKeyname}';

    private resourceUrl: string = SERVER_API_URL + 'api/p-tree-node-monitor/node-color-status/{nodeId}';
    private resourceUrlSuffix = '/status-details/{levelColorKeyname}';

    constructor(
        private http: HttpClient,
/*         private treeNavigateService: TreeNavigateService */
    ) {}

    loadCommonData(nodeId?: string): Observable<TreeNodeColorStatus[]> {
        const url = this.resourceUrl.replace(/{nodeId}/, nodeId);
        return this.http.get<TreeNodeColorStatus[]>(url);
    }

//        loadResourceData(nodeId, resourceName) {
////            var url = RESOURCE_DATA_URL.replace("{resource}", resource);
//            var url = this.resourceUrl.replace('{nodeId}', nodeId);
//            url += "?contServiceType=" + resourceName;
//            return $http.get(url);
//        }
//
//        loadNodeColorStatusDetails(nodeId, levelColor, resourceName) {
//            var url = (this.resourceUrl + this.resourceUrlSuffix).replace("{nodeId}", nodeId).replace("{levelColorKeyname}", levelColor);
//            if (angular.isDefined(resourceName) && resourceName !== null) {
//                url += "?contServiceType=" + resourceName;
//            }
//            return $http.get(url);
//        }
}
