package cn.arvix.vrdata.controller

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody;

import cn.arvix.vrdata.service.JpaShareService;;

@Controller
@RequestMapping("/t")
public class TestGController {
	@Autowired
	JpaShareService jpaShareService;
	
	@ResponseBody
	@RequestMapping("/u")
	public String index(Model model) {
		jpaShareService.executeForHql("update ModelData set modelData=? where caseId='N6HuPecF32y'" , update)
		return "admin/syncLog";
	} 
	
 String update = '''window.MP_PREFETCHED_MODELDATA = {
    "files": {
        "additional_files": [],
        "catalog_file": "catalog.json",
        "expires": 1474609295,
        "templates": ["http://127.0.0.1:8888/files287/d?path={{filename}}&caseId=N6HuPecF32y", "https://mp-app-prod.global.ssl.fastly.net?path={{filename}}&root=models/group_1258/job_400c3158-26b8-461b-8c37-ddd2b322a7c4/wf_5465b36db14041d191d36bd4f0719843/mesh/1.1.407.13667/2016-09-09_2039.56&sig=2465aabaa23db6c091986e31e2b95fa30b9045a6&exp=1474609295"],
        "type": "3"
    },
    "keys": {"mixpanel": "e7c32ea17a8feb158ea06196732d5a3b"},
    "model": {
        "address": {"address_1": "", "address_2": "", "city": "", "state": "", "zip": ""},
        "contact_email": "",
        "contact_name": "",
        "contact_phone": "",
        "created": "2016-09-09T18:25:38.954135Z",
        "external_url": "",
        "floors": "",
        "formatted_contact_phone": "",
        "image": "https://cdn-1.matterport.com/apifs/models/N6HuPecF32y/images/rz6FF7MPdKW/09.21.2016_17.39.04.jpg?sig=9ae841dbf5475b6f6473a6ab1a5ec174aa80482b&exp=1475127695",
        "images": [{
            "category": "tags",
            "created": "2016-09-21T09:40:59.500665Z",
            "download_url": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/EkLaKHCB9sJ.jpg",
            "height": 1080,
            "is_hero": false,
            "metadata": "{\"camera_mode\":0,\"camera_position\":{\"x\":-2.725241,\"y\":2.293198,\"z\":2.809459},\"camera_quaternion\":{\"x\":-0.07918585,\"y\":0.9007733,\"z\":-0.185441211,\"w\":-0.384641081},\"ortho_zoom\":-1,\"scan_id\":\"e0ee3010246640a6acc70c54f03d5ae5\",\"is_ortho\":false}",
            "modified": "2016-09-21T09:40:59.502881Z",
            "name": "09.21.2016_17.40.56",
            "owner_user": "252019bcbe3d97498df315eb2c5910",
            "sid": "EkLaKHCB9sJ",
            "signed_src": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/EkLaKHCB9sJ.jpg",
            "src": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/EkLaKHCB9sJ.jpg",
            "thumbnail_signed_src": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/EkLaKHCB9sJ.jpg",
            "url": "https://my.matterport.com/api/v1/player/models/N6HuPecF32y/images/EkLaKHCB9sJ",
            "width": 1920
        }, {
            "category": "tags",
            "created": "2016-09-21T09:39:43.967045Z",
            "download_url": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/aLrPDi3tHjc.jpg",
            "height": 1080,
            "is_hero": false,
            "metadata": "{\"camera_mode\":0,\"camera_position\":{\"x\":-2.725241,\"y\":2.293198,\"z\":2.809459},\"camera_quaternion\":{\"x\":-0.0118831778,\"y\":0.8580321,\"z\":-0.0198722612,\"w\":-0.5130739},\"ortho_zoom\":-1,\"scan_id\":\"e0ee3010246640a6acc70c54f03d5ae5\",\"is_ortho\":false}",
            "modified": "2016-09-21T09:39:43.968836Z",
            "name": "09.21.2016_17.39.42",
            "owner_user": "252019bcbe3d97498df315eb2c5910",
            "sid": "aLrPDi3tHjc",
            "signed_src": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/aLrPDi3tHjc.jpg",
            "src": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/aLrPDi3tHjc.jpg",
            "thumbnail_signed_src": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/aLrPDi3tHjc.jpg",
            "url": "https://my.matterport.com/api/v1/player/models/N6HuPecF32y/images/aLrPDi3tHjc",
            "width": 1920
        }, {
            "category": "user",
            "created": "2016-09-21T09:39:26.685037Z",
            "download_url": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/D13mveMroD8.jpg",
            "height": 1080,
            "is_hero": false,
            "metadata": "{\"camera_mode\":0,\"camera_position\":{\"x\":-2.725241,\"y\":2.293198,\"z\":2.809459},\"camera_quaternion\":{\"x\":-0.0118831778,\"y\":0.8580321,\"z\":-0.0198722612,\"w\":-0.5130739},\"ortho_zoom\":-1,\"scan_id\":\"e0ee3010246640a6acc70c54f03d5ae5\",\"is_ortho\":false}",
            "modified": "2016-09-21T09:39:26.685874Z",
            "name": "09.21.2016_17.39.25",
            "owner_user": "252019bcbe3d97498df315eb2c5910",
            "sid": "D13mveMroD8",
            "signed_src": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/D13mveMroD8.jpg",
            "src": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/D13mveMroD8.jpg",
            "thumbnail_signed_src": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/D13mveMroD8.jpg",
            "url": "https://my.matterport.com/api/v1/player/models/N6HuPecF32y/images/D13mveMroD8",
            "width": 1920
        }, {
            "category": "user",
            "created": "2016-09-21T09:39:07.270884Z",
            "download_url": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/rz6FF7MPdKW.jpg",
            "height": 1080,
            "is_hero": true,
            "metadata": "{\"camera_mode\":0,\"camera_position\":{\"x\":-1.27064,\"y\":1.628934,\"z\":3.028296},\"camera_quaternion\":{\"x\":-0.00487621827,\"y\":-0.728654,\"z\":-0.005188456,\"w\":0.684845},\"ortho_zoom\":-1,\"scan_id\":\"60954482085940fbb987bc56958ede72\",\"is_ortho\":false}",
            "modified": "2016-09-21T09:39:07.272701Z",
            "name": "09.21.2016_17.39.04",
            "owner_user": "252019bcbe3d97498df315eb2c5910",
            "sid": "rz6FF7MPdKW",
            "signed_src": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/rz6FF7MPdKW.jpg",
            "src": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/rz6FF7MPdKW.jpg",
            "thumbnail_signed_src": "http://vr.arvix.cn/upload/playerImages/N6HuPecF32y/playerImages/rz6FF7MPdKW.jpg",
            "url": "https://my.matterport.com/api/v1/player/models/N6HuPecF32y/images/rz6FF7MPdKW",
            "width": 1920
        }],
        "is_vr": false,
        "job": {"uuid": "400c315826b8461b8c37ddd2b322a7c4"},
        "metainfo": {"allowed_methods": ["GET", "OPTIONS", "HEAD"]},
        "modified": "2016-09-21T09:41:23.442575Z",
        "name": "Luodong Lege",
        "owner": {"group_sid": "group_i6j1iPcuhE4", "user_sid": "user_8wr4TCgL2ti"},
        "player_options": {
            "address": true,
            "autoplay": false,
            "contact_email": true,
            "contact_name": true,
            "contact_phone": true,
            "dollhouse": true,
            "external_url": true,
            "fast_transitions": true,
            "floor_plan": false,
            "highlight_reel": true,
            "labels": false,
            "model_name": true,
            "model_summary": true,
            "presented_by": true,
            "tour_buttons": true
        },
        "presented_by": "",
        "sid": "N6HuPecF32y",
        "social_sharing": false,
        "status": "viewable",
        "summary": "",
        "sweeps": {
            "17f63dea7e514892ada6b8d799b7493d": {"enabled": true},
            "1b249b36c02541d9812b1070752c3147": {"enabled": true},
            "24c9a64b36a742329a87d0fc71dd1490": {"enabled": true},
            "32dd1322cc4747e78220e7ff53daad78": {"enabled": true},
            "3a054771b5da4a5e904c285145a2fa08": {"enabled": true},
            "3de4113027384bea93f8832292c9c3a2": {"enabled": true},
            "4522cdd5cd254f74a7d7495d46a17945": {"enabled": true},
            "49286d9983c0412b8970bdf02ed7cd4c": {"enabled": true},
            "60954482085940fbb987bc56958ede72": {"enabled": true},
            "69a3091dc0d644c299f8b505081c0689": {"enabled": true},
            "6c44f88892d14cc98d6e98c70c079aaa": {"enabled": true},
            "6f822cb178ce477b9af2103dfd087a4d": {"enabled": true},
            "6fe79ee52470424d91faff5b5cb8267f": {"enabled": true},
            "7fb2123aa68645b9a3b6060e212418de": {"enabled": true},
            "8b0d2d30ae8d40c490ea3be712437544": {"enabled": true},
            "a2824afd6d0e4d48ba42468d5c4a4355": {"enabled": true},
            "bbed763a0d194b8db2bafa1b98b85c7c": {"enabled": true},
            "c1f06797a6824fce99f0994b4067103c": {"enabled": true},
            "ce50893aa26e4f6aaeb81901b4531b82": {"enabled": true},
            "dba2c2604f7b4f919388c62ca300101c": {"enabled": true},
            "e0ee3010246640a6acc70c54f03d5ae5": {"enabled": true},
            "e5395571aea84939878a35db4fd64f78": {"enabled": true},
            "ef28376c0d88403192ff163a058baa76": {"enabled": true},
            "fcc63abba6914d909c865fe4a3c58359": {"enabled": true}
        }
    },
    "user": {
        "flags": ["showcase_preheat_50", "panospheres", "floorplan", "billing_tab_hidden", "tag_links", "workshop_webgl", "show360views", "showcase_gzip_100", "mattertags", "model_settings"],
        "is_authenticated": false
    }
}'''
}
