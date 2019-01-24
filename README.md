Hot-Reload [![Build Status](https://travis-ci.com/gekomad/hot-reload.svg?token=1xbwh9MbFSDwkakXU9cP&branch=master)](https://travis-ci.com/gekomad/hot-reload)
[![codecov.io](http://codecov.io/github/gekomad/hot-reload/coverage.svg?branch=master)](http://codecov.io/github/gekomad/hot-reload?branch=master)  
[![Javadocs](https://javadoc.io/badge/com.github.gekomad/hot-reload_2.12.svg)](https://javadoc.io/doc/com.github.gekomad/hot-reload_2.12)  
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.gekomad/hot-reload_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.gekomad/hot-reload_2.12)  
======  

Hot-reload is a static/dynamic configurator based on [Config](https://github.com/lambdista/config).
  
## Add the library to your project  
`libraryDependencies += "com.github.gekomad" %% "hot-reload" % "0.3.0"`
  
## Motivations  
  
Generally, in a software project the majority of configuration files are static. This means that, to modify the parameters, the application must be deployed anew.
Although most parameters must not change as long as the application is live, others might change, even temporarily.

Hot-reload allows you to manage unmodifiable and modifiable configuration files.
  
## Using Library  
  
There are two modes: mutable and immutable.
The first one creates a configurator that modifies the parameters when the original conf file is modified.
The second one allows you to create a configurator that does not change when the original conf file is modified.
Two (or more) configuration files could be created (one for the parameters that can change and the other for the immutable parameters) and given to the application in this manner.

`./my_app -Dmutable_conf=/path/mutable.conf -Dimmutable_conf=/path/immutable.conf`  
  
  
### Examples  
  
##### First of all some definitions  
```  
 case class FooConfig(bar: String, baz: Option[Int], list: List[Int], missingValue: Option[String])  
```  
```  
file /path/conf1.conf:  
{  
   bar = "conf_1",  
   baz = 1,  
   list = [1, 1, 111]  
}  
  ```  
  ```  
file /path/conf2.conf:  
{  
   bar = "conf_2",  
   baz = 2,  
   list = [2, 2, 222]  
}  
```  
```  
file /path/confErr.conf:  
{  
   bar = 1, // this is an error, bar must be a String  
   baz = 42,  
   list = [1, 2, 3]  
}  
```  
  
### Create a mutable config  
  
```  
import com.github.gekomad.hotreload.core.HotReload  
copyFile(from = "/path/conf1.conf", to = "/path/mutable.conf")  

//create HotReload from conf1.conf file  
val hr: Try[HotReload[FooConfig]] = HotReload[FooConfig]("/path/mutable.conf")  

hr match {  
  case Success(hotReload) => {  
  
    {  // hotReload contains conf1  
     assert(hotReload.currentConf == FooConfig("conf_1", Some(1), List(1, 11, 111), None))
    }  
  
    {  
     Thread.sleep(1000)  
     copyFile(from = "/path/conf2.conf", to = "/path/mutable.conf")  
     Thread.sleep(1000)  
     assert(hotReload.currentConf == FooConfig("conf_2", Some(2), List(2, 22, 222), None))  
     // now hotReload contains conf2
    }  
  
    {     
     copyFile(from = "/path/confErr.conf", to = "/path/mutable.conf")  
     Thread.sleep(1000)  
     assert(hotReload.currentConf == FooConfig("conf_2", Some(2), List(2, 22, 222), None))  
     // hotReload still contains conf2 because confErr.conf file is wrong
    }  
  }
  case Failure(f) => println(s"error on loading file ${f.getMessage}")
}  
```  
  
### Create an immutable config  
  
```  
import com.github.gekomad.hotreload.core.HotReload  
    
copyFile(from = "/path/conf1.conf", to = "/path/immutable.conf")  
  
//create HotReload from conf1 file, set 'mutable = false' to make it immutable  
val hr: Try[HotReload[FooConfig]] = HotReload[FooConfig]("/path/immutable.conf", mutable = false)  
hr match {  
  case Success(hotReload) =>  
  
    // hotReload contains conf1  
    assert(hotReload.currentConf == FooConfig("conf_1", Some(1), List(1, 11, 111), None))  
  
    // modifying conf file, hotReload still contains conf1 because it's immutable
    copyFile(from = "/path/conf2.conf", to = "/path/immutable.conf")  
    Thread.sleep(1000)  
  
    assert(hotReload.currentConf == FooConfig("conf_1", Some(1), List(1, 11, 111), None))

  case Failure(f) => println(s"error on loading file ${f.getMessage}")
}  
```  
  
  
## Bugs and Feedback  
For bugs, questions and discussions please use [Github Issues](https://github.com/gekomad/hot-reload/issues).  
  
## License  
  
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance  
with the License. You may obtain a copy of the License at  
  
[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)  
  
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an  
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
See the License for the specific language governing permissions and limitations under the License.  
  
## Special Thanks ##  
  
To [lambdista](https://github.com/lambdista/config) for his library.