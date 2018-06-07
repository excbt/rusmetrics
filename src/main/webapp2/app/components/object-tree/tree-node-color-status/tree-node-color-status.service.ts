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

    private CHART_BG_COLORS: string[] = ['#ef473a', '#FDB45C', '#6e7b90', '#46BFBD', '#803690', '#337ab7'];
    private RESOURCES: string[] = ['heat', 'hw', 'cw', 'el'];
    private CHART_LABELS: string[] = ['Критические', 'Некритические', 'Штатные'];
    private STATUS_KEYNAMES: string[] = ['RED', 'YELLOW', 'GREEN'];

    constructor(
        private http: HttpClient,
/*         private treeNavigateService: TreeNavigateService */
    ) {}

    getChartBgColors(): string[] {
        return this.CHART_BG_COLORS;
    }

    getResources(): string[] {
        return this.RESOURCES;
    }

    getChartLabels(): string[] {
        return this.CHART_LABELS;
    }

    getStatusKeynames(): string[] {
        return this.STATUS_KEYNAMES;
    }

    loadCommonData(nodeId?: string): Observable<TreeNodeColorStatus[]> {
        const url = this.resourceUrl.replace(/{nodeId}/, nodeId);
        return this.http.get<TreeNodeColorStatus[]>(url);
    }

    loadResourceData(nodeId: string, resourceName: string) {
//            var url = RESOURCE_DATA_URL.replace("{resource}", resource);
        const url = this.resourceUrl.replace(/{nodeId}/, nodeId);
//        url += "?contServiceType=" + resourceName;
        return this.http.get<TreeNodeColorStatus[]>(url, {params: new HttpParams().set('contServiceType', resourceName) });
    }

    loadNodeColorStatusDetails(nodeId, levelColor, resourceName) {
        const url = (this.resourceUrl + this.resourceUrlSuffix).replace('{nodeId}', nodeId).replace('{levelColorKeyname}', levelColor);
//        if (angular.isDefined(resourceName) && resourceName !== null) {
//            url += "?contServiceType=" + resourceName;
//        }
        console.log('resourceName: ', resourceName);
        console.log('typeof resourceName: ', typeof resourceName);
        let getOpts = {};
        if (resourceName !== null) {
            getOpts = {
                params: new HttpParams().set('contServiceType', resourceName)
            }
        }
        
        return this.http.get<StatusDetails>(url, getOpts);
    }
}

export class StatusDetails {
    contObjectIds: number[];
}
